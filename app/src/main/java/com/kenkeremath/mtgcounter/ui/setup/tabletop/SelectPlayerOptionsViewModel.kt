package com.kenkeremath.mtgcounter.ui.setup.tabletop

import androidx.lifecycle.*
import com.kenkeremath.mtgcounter.model.player.PlayerColor
import com.kenkeremath.mtgcounter.model.player.PlayerSetupModel
import com.kenkeremath.mtgcounter.model.player.PlayerTemplateModel
import com.kenkeremath.mtgcounter.persistence.ProfileRepository
import com.kenkeremath.mtgcounter.ui.settings.profiles.manage.ProfileUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectPlayerOptionsViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private var loading = false

    private var allProfiles: Set<PlayerTemplateModel> = setOf()

    private val _profiles: MutableLiveData<List<ProfileUiModel>> =
        MutableLiveData(emptyList())
    val profiles: LiveData<List<ProfileUiModel>> = _profiles

    private val _setupModel: MutableLiveData<PlayerSetupModel> =
        MutableLiveData()
    val setupModel: LiveData<PlayerSetupModel> = _setupModel


    init {
        val setupModel =
            savedStateHandle.get<PlayerSetupModel>(SelectPlayerOptionsDialogFragment.ARGS_MODEL)
                ?: throw IllegalArgumentException("Model must be passed to ${javaClass.simpleName}")
        _setupModel.value = setupModel
        refresh()
    }

    fun refresh() {
        if (loading) {
            return
        }
        loading = true
        viewModelScope.launch {
            profileRepository.getAllPlayerTemplates()
                .catch {
                    loading = false
                    //TODO error handling?
                }
                .collect { data ->
                    loading = false
                    allProfiles = data.toSet()
                    _profiles.value = data.map {
                        ProfileUiModel(it)
                    }
                }
        }
    }

    fun updateColor(color: PlayerColor) {
        _setupModel.value = _setupModel.value?.copy(color = color)
    }

    fun updateProfile(profileName: String) {
        allProfiles.find {
            it.name == profileName
        }?.let {
            _setupModel.value = _setupModel.value?.copy(template = it)
        }
    }

}