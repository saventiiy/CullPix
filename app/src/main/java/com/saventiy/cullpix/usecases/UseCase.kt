package com.saventiy.cullpix.usecases

import com.saventiy.cullpix.repository.impl.Photo
import com.saventiy.cullpix.repository.impl.PhotoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPhotosUseCase @Inject constructor(private val repository: PhotoRepository) {
    operator fun invoke(): Flow<List<Photo>> = repository.getPhotos()
}

class DeletePhotoUseCase @Inject constructor(private val repository: PhotoRepository) {
    suspend operator fun invoke(photo: Photo) = repository.deletePhoto(photo)
}