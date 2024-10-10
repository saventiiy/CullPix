package com.saventiy.cullpix.views.state

import androidx.documentfile.provider.DocumentFile
import java.io.File

sealed class MediaIntent {
    data class DeleteMediaFile(val mediaFile: File) : MediaIntent()
}