package com.saventiy.cullpix.repository

import kotlinx.coroutines.flow.Flow
import java.io.File

interface MediaRepository {
    fun deleteMediaFile(media: File): Flow<String>
    fun loadMediaFiles(directoryPath: String): Flow<List<File>>
}