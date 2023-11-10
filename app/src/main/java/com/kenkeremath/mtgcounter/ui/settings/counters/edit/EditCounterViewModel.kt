package com.kenkeremath.mtgcounter.ui.settings.counters.edit

import androidx.lifecycle.*
import com.kenkeremath.mtgcounter.livedata.SingleLiveEvent
import com.kenkeremath.mtgcounter.model.counter.CounterModel
import com.kenkeremath.mtgcounter.model.counter.CounterTemplateModel
import com.kenkeremath.mtgcounter.persistence.ProfileRepository
import com.kenkeremath.mtgcounter.persistence.images.ImageRepository
import com.kenkeremath.mtgcounter.persistence.images.ImageSource
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
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val profileToLink: String? =
        savedStateHandle[EditCounterDialogFragment.ARGS_PROFILE_NAME]

    private var _editedCounterTemplate =
        savedStateHandle[EditCounterDialogFragment.ARGS_COUNTER_TEMPLATE]
        //TODO: time abstraction
            ?: CounterTemplateModel(dateAdded = Date(), isFullArtImage = true)

    // Use to diff for changes
    val startingCounterTemplate = _editedCounterTemplate.copy()

    val counterTemplate: CounterTemplateModel
        get() = _editedCounterTemplate

    // URL images are saved locally. so if we are editing something it will never have URL type
    private val _selectedCounterType: MutableLiveData<CreateCounterType> =
        MutableLiveData<CreateCounterType>(
            if (!_editedCounterTemplate.name.isNullOrBlank())
                CreateCounterType.TEXT
            else
                CreateCounterType.IMAGE
        )
    val selectedCounterType: LiveData<CreateCounterType> = _selectedCounterType

    private val _counterLabel: MutableLiveData<String> =
        MutableLiveData<String>(_editedCounterTemplate.name ?: "")
    val counterLabel: LiveData<String> = _counterLabel

    private val _counterUrl: MutableLiveData<String> = MutableLiveData<String>("")
    val counterUrl: LiveData<String> = _counterUrl

    // May be a local image or URI
    val resolvedImageLocation = MediatorLiveData<String?>()
    private val _counterImageUri: MutableLiveData<String> =
        MutableLiveData<String>(_editedCounterTemplate.uri)
    private val _counterImageFileName: MutableLiveData<String> = MutableLiveData<String>("")

    private val _isFullArtImage: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>(_editedCounterTemplate.isFullArtImage)
    val isFullArtImage: LiveData<Boolean> = _isFullArtImage

    private val _startingValue: MutableLiveData<Int> =
        MutableLiveData<Int>(_editedCounterTemplate.startingValue)
    val startingValue: LiveData<Int> = _startingValue

    //Hide in view if null
    private val _counterPreview: MutableLiveData<CounterModel?> =
        MutableLiveData<CounterModel?>(null)
    val counterPreview: LiveData<CounterModel?> = _counterPreview

    private val _saveEnabled: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val saveEnabled: LiveData<Boolean> = _saveEnabled

    private val _saveStatus: SingleLiveEvent<SaveCounterResult> = SingleLiveEvent()
    val saveStatus: LiveData<SaveCounterResult> = _saveStatus

    init {
        resolvedImageLocation.addSource(_counterImageUri) { uri ->
            val fileNameValue = _counterImageFileName.value
            if (!uri.isNullOrBlank()) {
                resolvedImageLocation.value = uri
            } else  {
                resolvedImageLocation.value =  fileNameValue
            }
        }
        resolvedImageLocation.addSource(_counterImageFileName) { fileName ->
            val uriValue = _counterImageUri.value
            if (!fileName.isNullOrBlank()) {
                resolvedImageLocation.value = fileName
            } else  {
                resolvedImageLocation.value =  uriValue
            }
        }
        updateUi()
    }

    fun selectCounterType(type: CreateCounterType) {
        _selectedCounterType.value = type
        updateUi()
    }

    fun updateLabel(label: String) {
        _counterLabel.value = label
        updateUi()
    }

    fun updateUrl(url: String) {
        val resolvedUrl = url.let {
            if (it.isBlank())
                ""
            else if (!it.contains("http")) {
                ""
            } else {
                it.trim()
            }
        }
        _counterUrl.value = resolvedUrl
        updateUi()
    }

    fun updateLocalUri(uri: String, fileName: String) {
        _counterImageUri.value = uri
        _counterImageFileName.value = fileName
        updateUi()
    }

    fun setIsFullArtImage(fullArt: Boolean) {
        _isFullArtImage.value = fullArt
        updateUi()
    }

    fun updateStartingValue(startingValue: String) {
        val parsedValue = try {
            Integer.parseInt(startingValue)
        } catch (_: Exception) {
            0
        }
        _startingValue.value = parsedValue
        updateUi()
    }

    private fun updateUi() {
        var saveEnabled = false
        when (_selectedCounterType.value) {
            CreateCounterType.TEXT -> {
                saveEnabled = !_counterLabel.value.isNullOrBlank()
                _editedCounterTemplate = _editedCounterTemplate.copy(
                    name = _counterLabel.value,
                    uri = null,
                    startingValue = _startingValue.value ?: 0,
                    isFullArtImage = _isFullArtImage.value == true,
                )
            }
            CreateCounterType.IMAGE -> {
                val uri = _counterImageUri.value.let { if (it.isNullOrBlank()) null else it }
                saveEnabled = !uri.isNullOrBlank()
                _editedCounterTemplate =
                    _editedCounterTemplate.copy(
                        name = null,
                        uri = uri,
                        startingValue = _startingValue.value ?: 0,
                        isFullArtImage = _isFullArtImage.value == true,
                    )
            }
            CreateCounterType.URL -> {
                val uri = _counterUrl.value.let { if (it.isNullOrBlank()) null else it }
                saveEnabled = !uri.isNullOrBlank()
                _editedCounterTemplate = _editedCounterTemplate.copy(
                    name = null,
                    uri = uri,
                    startingValue = _startingValue.value ?: 0,
                    isFullArtImage = _isFullArtImage.value == true,
                )
            }
            null -> {}
        }
        generatePreview()
        _saveEnabled.value = saveEnabled
    }

    private fun generatePreview() {
        _counterPreview.value =
            CounterModel(_editedCounterTemplate.startingValue, _editedCounterTemplate)
    }

    fun save() {
        if (_saveEnabled.value != true) {
            return
        }
        viewModelScope.launch {
            /**
             * Save image first to generate local file path. set that as URI before saving template
             * to database. For some images (GIFs currently), we will not save locally and will just
             * use the raw uri or url. This has the side effect that those source images could be removed.
             * In that case, the user will receive an error and tehy will have to remake the token
             *
             * If the image fails to save, the service will automatically clean up temporary files
             */
            val uri = _editedCounterTemplate.uri
            val saveImageFlow =
                if (uri == null || _editedCounterTemplate.uri == startingCounterTemplate.uri) {
                    flowOf(true)
                } else if (uri.startsWith("http")) {
                    imageRepository.saveUrlImageToDisk(uri)
                        .map {
                            if (it.source == ImageSource.LOCAL_FILE) {
                                _editedCounterTemplate =
                                    _editedCounterTemplate.copy(uri = it.file?.absolutePath)
                            }
                        }
                } else {
                    imageRepository.saveLocalImageToDisk(uri)
                        .map {
                            if (it.source == ImageSource.LOCAL_FILE) {
                                _editedCounterTemplate =
                                    _editedCounterTemplate.copy(uri = it.file?.absolutePath)
                            }
                        }
                }

            saveImageFlow.flatMapConcat {
                if (profileToLink != null) {
                    profileRepository.addCounterTemplateToProfile(
                        _editedCounterTemplate,
                        profileToLink
                    )
                } else {
                    profileRepository.addCounterTemplate(_editedCounterTemplate)
                }
            }
                .catch {
                    LogUtils.d("Failed to save counter")
                    _saveStatus.value = SaveCounterResult.IMAGE_SAVE_FAILED
                }
                .collect { generatedId ->
                    _editedCounterTemplate = _editedCounterTemplate.copy(id = generatedId)
                    LogUtils.d("Counter successfully saved: ${_editedCounterTemplate.id}")
                    _saveStatus.value = SaveCounterResult.SUCCESSFUL
                }
        }
    }
}