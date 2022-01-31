package com.kenkeremath.mtgcounter.ui.settings.counters.edit

import androidx.lifecycle.*
import com.kenkeremath.mtgcounter.livedata.SingleLiveEvent
import com.kenkeremath.mtgcounter.model.counter.CounterModel
import com.kenkeremath.mtgcounter.model.counter.CounterTemplateModel
import com.kenkeremath.mtgcounter.persistence.ProfileRepository
import com.kenkeremath.mtgcounter.persistence.images.ImageRepository
import com.kenkeremath.mtgcounter.util.LogUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class EditCounterViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val imageRepository: ImageRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val profileToLink: String? =
        savedStateHandle.get(EditCounterDialogFragment.ARGS_PROFILE_NAME)

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

    private val _isFullArtImage: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val isFullArtImage: LiveData<Boolean> = _isFullArtImage

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

    fun setIsFullArtImage(fullArt: Boolean) {
        _isFullArtImage.value = fullArt
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
                    isFullArtImage = _isFullArtImage.value == true,
                )
            }
            CreateCounterType.IMAGE -> {
                _newCounterTemplate =
                    _newCounterTemplate.copy(
                        name = null,
                        uri = _counterImageUri.value,
                        startingValue = _startingValue.value ?: 0,
                        isFullArtImage = _isFullArtImage.value == true,
                    )
            }
            CreateCounterType.URL -> {
                _newCounterTemplate = _newCounterTemplate.copy(
                    name = null,
                    uri = _counterUrl.value,
                    startingValue = _startingValue.value ?: 0,
                    isFullArtImage = _isFullArtImage.value == true,
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
            /**
             * Save image first to generate local file path. set that as URI before saving template
             * to database.
             *
             * If the image fails to save, the service will automatically clean up temporary files
             */
            val uri = _newCounterTemplate.uri
            val saveImageFlow = if (uri == null) {
                flowOf(true)
            } else if (uri.startsWith("http")) {
                imageRepository.saveUrlImageToDisk(uri)
                    .map {
                        _newCounterTemplate = _newCounterTemplate.copy(uri = it.absolutePath)
                    }
            } else {
                imageRepository.saveLocalImageToDisk(uri)
                    .map {
                        _newCounterTemplate = _newCounterTemplate.copy(uri = it.absolutePath)
                    }
            }

            saveImageFlow.flatMapConcat {
                if (profileToLink != null) {
                    profileRepository.addCounterTemplateToProfile(
                        _newCounterTemplate,
                        profileToLink
                    )
                } else {
                    profileRepository.addCounterTemplate(_newCounterTemplate)
                }
            }
                .catch {
                    LogUtils.d("Failed to save counter")
                    _saveStatus.value = SaveCounterResult.IMAGE_SAVE_FAILED
                }
                .collect {
                    LogUtils.d("Counter successfully saved: ${_newCounterTemplate.id}")
                    _saveStatus.value = SaveCounterResult.SUCCESSFUL
                }
        }
    }
}