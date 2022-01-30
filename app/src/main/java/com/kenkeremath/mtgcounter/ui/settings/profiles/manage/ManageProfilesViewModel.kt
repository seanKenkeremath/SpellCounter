package com.kenkeremath.mtgcounter.ui.settings.profiles.manage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kenkeremath.mtgcounter.model.player.PlayerTemplateModel
import com.kenkeremath.mtgcounter.persistence.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageProfilesViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private var loading = false

    private var allProfiles: Set<PlayerTemplateModel>? = null

    private val _profiles: MutableLiveData<List<ProfileUiModel>> = MutableLiveData(emptyList())
    val profiles: LiveData<List<ProfileUiModel>> = _profiles

    init {
        refreshProfiles()
    }

    fun refreshProfiles() {
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
                .collect {
                    loading = false
                    allProfiles = it.toSet()
                    _profiles.value = it.sorted().map { template ->
                        ProfileUiModel(
                            template
                        )
                    }
                }
        }
    }

    fun getProfileByName(name: String): PlayerTemplateModel? {
        return allProfiles?.find { it.name == name }
    }
}