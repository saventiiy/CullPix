package com.saventiy.cullpix.views

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saventiy.cullpix.usecases.DeleteMediaFileUseCase
import com.saventiy.cullpix.usecases.LoadMediaFilesUseCase
import com.saventiy.cullpix.views.state.MediaIntent
import com.saventiy.cullpix.views.state.MediaState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PhotoViewModel @Inject constructor(
    private val loadMediaFilesUseCase: LoadMediaFilesUseCase,
    private val deleteMediaFileUseCase: DeleteMediaFileUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val DIRECTORY_PATH_KEY = "DIRECTORY_PATH_KEY"
    }

    private val _state = MutableStateFlow(MediaState())
    val state: StateFlow<MediaState> = _state.asStateFlow()

    init {
        savedStateHandle.get<String>(DIRECTORY_PATH_KEY)?.let { directoryPath ->
            loadMediaFiles(directoryPath)
        }
    }

    fun onIntent(intent: MediaIntent) {
        when (intent) {
            is MediaIntent.DeleteMediaFile -> deleteMediaFile(intent.mediaFile)
        }
    }

    fun setDirectoryUri(directoryPath: String) {
        savedStateHandle[DIRECTORY_PATH_KEY] = directoryPath
        loadMediaFiles(directoryPath)
    }

    private fun loadMediaFiles(directoryPath: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                loadMediaFilesUseCase.invoke(directoryPath).collect() { media ->
                    _state.update { it.copy(mediaFiles = media) }
                }
            }
        }
    }

    private fun deleteMediaFile(media: File) {
        viewModelScope.launch {
            deleteMediaFileUseCase.invoke(media).collect() { result ->
                _state.update { it.copy(mediaFiles = it.mediaFiles.filter { it.exists() }) }
            }
        }
    }
}