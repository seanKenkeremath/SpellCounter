package com.kenkeremath.mtgcounter.ui.settings.counters.manage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kenkeremath.mtgcounter.model.counter.CounterTemplateModel
import com.kenkeremath.mtgcounter.persistence.ProfileRepository
import com.kenkeremath.mtgcounter.persistence.images.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageCountersViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val imageRepository: ImageRepository,
) : ViewModel() {

    private var loading = false

    private var customCounters: MutableSet<CounterTemplateModel>? = null

    private val _counters: MutableLiveData<List<ManageCounterUiModel>> =
        MutableLiveData(emptyList())
    val counters: LiveData<List<ManageCounterUiModel>> = _counters

    init {
        refreshCounters()
    }

    fun refreshCounters() {
        if (loading) {
            return
        }
        loading = true
        viewModelScope.launch {
            profileRepository.getAllCounters()
                .map { allCounters ->
                    allCounters.filter {
                        it.deletable
                    }
                }
                .catch {
                    loading = false
                    //TODO error handling?
                }
                .collect {
                    loading = false
                    customCounters = it.toMutableSet()
                    updateUi()
                }
        }
    }

    private fun updateUi() {
        _counters.value = customCounters?.sorted()?.map { template ->
            ManageCounterUiModel(template)
        }
    }

    fun addNewCounter(counterTemplateModel: CounterTemplateModel) {
        customCounters?.add(counterTemplateModel)
        updateUi()
    }

    fun removeCounter(counterId: Int) {
        customCounters?.find {
            it.id == counterId
        }?.let { template ->
            viewModelScope.launch {
                profileRepository.deleteCounterTemplate(counterId)
                    .map {
                        if (it && template.uri != null) {
                            imageRepository.deleteFile(template.uri)
                        }
                        it
                    }
                    .catch {}
                    .collect {
                        if (it) {
                            refreshCounters()
                        }
                    }
            }
        }
    }
}