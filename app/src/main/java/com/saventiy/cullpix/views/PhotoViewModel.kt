package com.saventiy.cullpix.views

import android.app.PendingIntent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saventiy.cullpix.repository.impl.Photo
import com.saventiy.cullpix.repository.impl.PhotoDeletionException
import com.saventiy.cullpix.usecases.DeletePhotoUseCase
import com.saventiy.cullpix.usecases.GetPhotosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoViewModel @Inject constructor(
    private val getPhotosUseCase: GetPhotosUseCase,
    private val deletePhotoUseCase: DeletePhotoUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PhotoState())
    val state: StateFlow<PhotoState> = _state.asStateFlow()

    init {
        loadPhotos()
    }

    private fun loadPhotos() {
        viewModelScope.launch {
            getPhotosUseCase().collect { photos ->
                _state.update { it.copy(photos = photos) }
            }
        }
    }

    fun onIntent(intent: PhotoIntent) {
        when (intent) {
            is PhotoIntent.DeletePhoto -> deletePhoto(intent.photo)
            is PhotoIntent.KeepPhoto -> keepPhoto(intent.photo)
        }
    }

    private fun deletePhoto(photo: Photo) {
        viewModelScope.launch {
            try {
                deletePhotoUseCase(photo)
                _state.update {
                    it.copy(photos = it.photos.filter { p -> p.id != photo.id })
                }
            } catch (e: PhotoDeletionException) {
                _state.update { it.copy(deletionPendingIntent = e.pendingIntent) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    private fun keepPhoto(photo: Photo) {
        // In this case, we don't need to do anything as we're just keeping the photo
        // You could add some logic here if needed, like marking the photo as "favorite"
    }
}

data class PhotoState(
    val photos: List<Photo> = emptyList(),
    val error: String? = null,
    val deletionPendingIntent: PendingIntent? = null
)

sealed class PhotoIntent {
    data class DeletePhoto(val photo: Photo) : PhotoIntent()
    data class KeepPhoto(val photo: Photo) : PhotoIntent()
}