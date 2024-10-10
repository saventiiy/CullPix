package com.saventiy.cullpix.repository

import com.saventiy.cullpix.utils.isMediaFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor() : MediaRepository {

    override fun deleteMediaFile(
        media: File,
    ): Flow<String> = flow {
        if (media.delete()) {
            emit("Deleted")
        } else {
            emit("Error while deleting")
        }
    }

    override fun loadMediaFiles(directoryPath: String): Flow<List<File>> = flow {
        val mediaFiles = mutableListOf<File>()
        val directory = File(directoryPath)
        traversDirectory(directory, mediaFiles)
        emit(mediaFiles)
    }

    private suspend fun traversDirectory(
        directory: File?,
        mediaFiles: MutableList<File>
    ) {
        directory?.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                traversDirectory(file, mediaFiles)
            } else if (isMediaFile(file)) {
                mediaFiles.add(file)
            }
        }
    }
}