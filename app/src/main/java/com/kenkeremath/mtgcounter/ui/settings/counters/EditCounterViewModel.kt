package com.kenkeremath.mtgcounter.ui.settings.counters

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kenkeremath.mtgcounter.livedata.SingleLiveEvent
import com.kenkeremath.mtgcounter.model.counter.CounterModel
import com.kenkeremath.mtgcounter.model.counter.CounterTemplateModel
import com.kenkeremath.mtgcounter.persistence.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class EditCounterViewModel @Inject constructor(private val profileRepository: ProfileRepository) :
    ViewModel() {

    //TODO: edit existing?

    //TODO: time abstraction
    private var _newCounterTemplate = CounterTemplateModel(dateAdded = Date())
    val counterTemplate: CounterTemplateModel
        get() = _newCounterTemplate

    private val _selectedCounterType: MutableLiveData<CreateCounterType> =
        MutableLiveData<CreateCounterType>(CreateCounterType.TEXT)
    val selectedCounterType: LiveData<CreateCounterType> = _selectedCounterType

    private val _counterLabel: MutableLiveData<String> = MutableLiveData<String>("")
    val counterLabel: LiveData<String> = _counterLabel

    private val _counterUrl: MutableLiveData<String> = MutableLiveData<String>("")
    val counterUrl: LiveData<String> = _counterUrl

    private val _counterImageUri: MutableLiveData<String> = MutableLiveData<String>("")
    val counterImageUri: LiveData<String> = _counterImageUri

    private val _startingValue: MutableLiveData<Int> = MutableLiveData<Int>(0)
    val startingValue: LiveData<Int> = _startingValue

    //Hide in view if null
    private val _counterPreview: MutableLiveData<CounterModel?> =
        MutableLiveData<CounterModel?>(null)
    val counterPreview: LiveData<CounterModel?> = _counterPreview

    private val _saveStatus: SingleLiveEvent<SaveCounterResult> = SingleLiveEvent()
    val saveStatus: LiveData<SaveCounterResult> = _saveStatus

    fun selectCounterType(type: CreateCounterType) {
        _selectedCounterType.value = type
        updateTemplate()
    }

    fun updateLabel(label: String) {
        _counterLabel.value = label
        updateTemplate()
    }

    fun updateUrl(url: String) {
        _counterUrl.value = url
        updateTemplate()
    }

    fun updateLocalUri(uri: String) {
        _counterImageUri.value = uri
        updateTemplate()
    }

    fun updateStartingValue(startingValue: String) {
        val parsedValue = try {
            Integer.parseInt(startingValue)
        } catch (_: Exception) {
            0
        }
        _startingValue.value = parsedValue
        updateTemplate()
    }

    private fun updateTemplate() {
        when (_selectedCounterType.value) {
            CreateCounterType.TEXT -> {
                _newCounterTemplate = _newCounterTemplate.copy(
                    name = _counterLabel.value,
                    uri = null,
                    startingValue = _startingValue.value ?: 0,
                )
            }
            CreateCounterType.IMAGE -> {
                _newCounterTemplate =
                    _newCounterTemplate.copy(
                        name = null,
                        uri = _counterImageUri.value,
                        startingValue = _startingValue.value ?: 0,
                    )
            }
            CreateCounterType.URL -> {
                _newCounterTemplate = _newCounterTemplate.copy(
                    name = null,
                    uri = _counterUrl.value,
                    startingValue = _startingValue.value ?: 0,
                )
            }
            null -> {}
        }
        generatePreview()
    }

    private fun generatePreview() {
        _counterPreview.value = CounterModel(_newCounterTemplate.startingValue, _newCounterTemplate)
    }

    fun save() {
        viewModelScope.launch {
            profileRepository.addCounterTemplate(_newCounterTemplate)
                .catch {
                    //TODO
                }
                .collect { generatedId ->
                    _newCounterTemplate = _newCounterTemplate.copy(id = generatedId)
                    _saveStatus.value = SaveCounterResult.SUCCESSFUL
                }
        }
    }
}