package com.saventiy.cullpix.repository.impl

import android.app.PendingIntent
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface PhotoRepository {
    fun getPhotos(): Flow<List<Photo>>
    suspend fun deletePhoto(photo: Photo)
}

class PhotoRepositoryImpl @Inject constructor(
    private val context: Context
) : PhotoRepository {

    private val contentResolver: ContentResolver = context.contentResolver

    override fun getPhotos(): Flow<List<Photo>> = flow {
        val photos = mutableListOf<Photo>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_TAKEN
        )
        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dateTakenColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val dateTaken = cursor.getLong(dateTakenColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                photos.add(Photo(id, name, contentUri, dateTaken))
            }
        }
        emit(photos)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override suspend fun deletePhoto(photo: Photo) {
        withContext(Dispatchers.IO) {
            try {
                val pintent = MediaStore.createDeleteRequest(contentResolver, listOf(photo.uri)).send()
//                contentResolver.delete(photo.uri, null, null)
            } catch (e: SecurityException) {

                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                        val pintent = MediaStore.createDeleteRequest(contentResolver, listOf(photo.uri)).send()
                        Log.e("ERR", pintent.toString())
                        Log.e("ERR", e.toString())
//                    throw PhotoDeletionException(pendingIntent)
                    }
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                        Log.e("ERR", e.toString())
                    }
                    else -> {
                        throw e
                    }
                }
            }
        }
    }
}

data class Photo(
    val id: Long,
    val name: String,
    val uri: Uri,
    val dateTaken: Long
)

class PhotoDeletionException(val pendingIntent: PendingIntent) : Exception("Permission needed to delete photo")