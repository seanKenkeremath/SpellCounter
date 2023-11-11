package com.kenkeremath.mtgcounter.ui.settings.profiles.edit

import androidx.lifecycle.*
import com.kenkeremath.mtgcounter.livedata.SingleLiveEvent
import com.kenkeremath.mtgcounter.model.counter.CounterTemplateModel
import com.kenkeremath.mtgcounter.model.player.PlayerProfileModel
import com.kenkeremath.mtgcounter.persistence.ProfileRepository
import com.kenkeremath.mtgcounter.view.counter.edit.CounterSelectionUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val originalProfile: PlayerProfileModel? =
        savedStateHandle.get<PlayerProfileModel>(EditProfileDialogFragment.ARGS_PROFILE)
    private var editedProfile = originalProfile ?: PlayerProfileModel()

    val originalProfileName
        get() = originalProfile?.name

    val isNewProfile = originalProfile == null
    val hasEdits: Boolean
        get() = originalProfile != editedProfile

    //Disable name change for all existing profiles
    //TODO: if we use a separate ID as a primary key for profile names, we can allow edits
    val isNameChangeEnabled = originalProfile == null

    private var newProfileInitializedWithCounters = false
    private var loading = false

    private var allCounters: MutableSet<CounterTemplateModel>? = null
    val availableCounters: List<CounterTemplateModel>
        get() = allCounters?.toList() ?: emptyList()

    private var allProfiles: Set<PlayerProfileModel>? = null

    private val _counterSelections: MutableLiveData<List<CounterSelectionUiModel>> =
        MutableLiveData(emptyList())
    val counterSelections: LiveData<List<CounterSelectionUiModel>> = _counterSelections

    private val _profileName: MutableLiveData<String> = MutableLiveData(editedProfile.name)
    val profileName: LiveData<String> = _profileName

    private val _saveButtonEnabled: MutableLiveData<Boolean> = MutableLiveData(false)
    val saveButtonEnabled: LiveData<Boolean> = _saveButtonEnabled

    private val _saveStatus: SingleLiveEvent<SaveProfileResult> = SingleLiveEvent()
    val saveStatus: LiveData<SaveProfileResult> = _saveStatus

    init {
        refresh()
    }

    fun refresh() {
        if (loading) {
            return
        }
        loading = true
        viewModelScope.launch {
            profileRepository.getAllCounters()
                .zip(profileRepository.getAllPlayerProfiles()) { counters, players ->
                    Pair(counters, players)
                }
                .catch {
                    loading = false
                    //TODO error handling?
                }
                .collect { data ->
                    loading = false
                    allCounters = data.first.toMutableSet()
                    allProfiles = data.second.toSet()
                    if (isNewProfile && !newProfileInitializedWithCounters) {
                        //Set new profile to match counters in default profile
                        editedProfile = editedProfile.copy(
                            counters = allProfiles?.find { it.name == PlayerProfileModel.NAME_DEFAULT }?.counters
                                ?: emptyList()
                        )
                        newProfileInitializedWithCounters = true
                    }
                    updateUi()
                }
        }
    }

    private fun updateUi() {
        _counterSelections.value = allCounters?.sortedDescending()?.map { template ->
            CounterSelectionUiModel(
                template,
                editedProfile.counters.find { it.id == template.id } != null
            )
        }
        _saveButtonEnabled.value =
            originalProfile != editedProfile && editedProfile.name.isNotBlank()
    }

    fun updateName(name: String) {
        if (originalProfile?.deletable == false) {
            return
        }
        editedProfile = editedProfile.copy(name = name.trim())
        _profileName.value = editedProfile.name
        updateUi()
    }

    fun addNewCounter(counterTemplateModel: CounterTemplateModel) {
        allCounters?.add(counterTemplateModel)
        editedProfile =
            editedProfile.copy(counters = editedProfile.counters.plus(counterTemplateModel))
        updateUi()
    }

    fun selectCounter(id: Int, select: Boolean) {
        getCounterById(id)?.let { counter ->
            if (!select) {
                if (editedProfile.counters.find { it.id == counter.id } == null) {
                    return
                } else {
                    editedProfile =
                        editedProfile.copy(counters = editedProfile.counters.filter { it.id != counter.id })
                }
            } else {
                if (editedProfile.counters.find { it.id == counter.id } != null) {
                    return
                } else {
                    editedProfile =
                        editedProfile.copy(counters = editedProfile.counters.plus(counter))
                }
            }
            updateUi()
        }
    }

    fun saveChanges(replaceExisting: Boolean = false) {
        if (editedProfile.name.isBlank()) {
            _saveStatus.value = SaveProfileResult.INVALID_NAME
            return
        }
        val newProfile = originalProfile == null
        val differentName = newProfile || editedProfile.name.trim() != originalProfile!!.name.trim()
        if (differentName) {
            allProfiles?.let { profiles ->
                val existing = profiles.find { it.name == editedProfile.name }
                existing?.let { conflictingProfile ->
                    if (!conflictingProfile.deletable) {
                        _saveStatus.value = SaveProfileResult.NAME_CONFLICT_CANNOT_REPLACE
                        return
                    } else if (!replaceExisting) {
                        _saveStatus.value = SaveProfileResult.NAME_CONFLICT
                        return
                    }
                }
            }
        }
        viewModelScope.launch {
            val removeOld = if (!newProfile && differentName)
                profileRepository.deletePlayerProfile(originalProfile!!.name)
            else flowOf(true)
            removeOld.zip(profileRepository.addPlayerProfile(editedProfile)) { _, _ -> }.collect {
                _saveStatus.value = SaveProfileResult.SUCCESSFUL
            }
        }
    }

    private fun getCounterById(id: Int): CounterTemplateModel? {
        return allCounters?.find { it.id == id }
    }
}