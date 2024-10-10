package com.saventiy.cullpix.utils

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import java.io.File

internal fun requestPermissions(activity: Activity) {
    val permissions = if (Build.VERSION.SDK_INT >= 33) {
        arrayOf(
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
        )
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
    }

    ActivityCompat.requestPermissions(
        activity,
        permissions,
        0
    )
}


@RequiresApi(Build.VERSION_CODES.O)
internal fun openDirectory(
    openDocumentTreeLauncher: ActivityResultLauncher<Intent>
) {
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
        putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.DIRECTORY_DOWNLOADS)
    }
    openDocumentTreeLauncher.launch(intent)
}

internal fun getMimeType(url: String?): String? {
    var type: String? = null
    val extension = MimeTypeMap.getFileExtensionFromUrl(url)
    if (extension != null) {
        type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }
    return type
}

internal fun isMediaFile(file: File): Boolean {
    val mimeType = getMimeType(file.path)
    return mimeType?.startsWith("image/") == true
//                || mimeType?.startsWith("video/") == true
}