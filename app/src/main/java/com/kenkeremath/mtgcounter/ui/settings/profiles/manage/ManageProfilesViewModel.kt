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

    private var allProfiles: MutableSet<PlayerTemplateModel>? = null

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
                    allProfiles = it.toMutableSet()
                    generateUiModels()
                }
        }
    }

    private fun generateUiModels() {
        _profiles.value = allProfiles?.sorted()?.map { template ->
            ProfileUiModel(
                template
            )
        } ?: emptyList()
    }

    fun getProfileByName(name: String): PlayerTemplateModel? {
        return allProfiles?.find { it.name == name }
    }

    fun deleteProfile(name: String) {
        viewModelScope.launch {
            //optimistic removal from list for better responsiveness
            allProfiles?.removeAll { it.name == name }
            generateUiModels()
            profileRepository.deletePlayerTemplate(name)
                .catch { }
                .collect {
                    //No Op
                }
        }
    }
}