package com.saventiy.cullpix.usecases

import com.saventiy.cullpix.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject

class DeleteMediaFileUseCase @Inject constructor(private val repository: MediaRepository) {
    operator fun invoke(media: File): Flow<String> = repository.deleteMediaFile(media)
}

class LoadMediaFilesUseCase @Inject constructor(private val repository: MediaRepository) {
    operator fun invoke(directoryPath: String): Flow<List<File>> =
        repository.loadMediaFiles(directoryPath)
}