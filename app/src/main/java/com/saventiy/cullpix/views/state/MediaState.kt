package com.saventiy.cullpix.views.state

import androidx.documentfile.provider.DocumentFile
import java.io.File

data class MediaState(
    val mediaFiles: List<File> = emptyList(),
    val error: String? = null,
)