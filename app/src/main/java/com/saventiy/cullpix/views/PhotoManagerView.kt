package com.saventiy.cullpix.views

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import coil.compose.rememberAsyncImagePainter
import com.saventiy.cullpix.views.state.MediaIntent
import java.io.File

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun PhotoManagerScreen(viewModel: PhotoViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            modifier = Modifier.padding(50.dp), text = state.mediaFiles.size.toString()
        )
        if (state.mediaFiles.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("No photos found")
                Button(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                    onClick = {
                        viewModel.setDirectoryUri(
                            File(
                                Environment.getExternalStorageDirectory(),
                                ""
                            ).path
                        )
                    }
                ) {
                    Text(text = "Open")
                }

                /*
                TODO just interesting
                ACTION_MANAGE_APP - gonna show only app with specific packageName
                ACTION_MANAGE_ALL - gonna show list of apps where u could choose

                */
                Button(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp), onClick = {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.data = Uri.fromParts("package", context.packageName, null)

                    context.startActivity(intent)
                }) {
                    Text(text = "Open")
                }
            }
        } else {
            PhotoSwiper(photos = state.mediaFiles, onPhotoSwiped = { photo, direction ->
                when (direction) {
                    SwipeDirection.LEFT -> viewModel.onIntent(MediaIntent.DeleteMediaFile(photo))
                    SwipeDirection.RIGHT -> {}
                }
            })
        }

        state.error?.let { error ->
            Text(
                error,
                color = Color.Red,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun PhotoSwiper(
    photos: List<File>,
    onPhotoSwiped: (File, SwipeDirection) -> Unit
) {
    var currentIndex by remember { mutableStateOf(0) }
    val currentPhoto = photos.getOrNull(currentIndex)

    if (currentPhoto != null) {
        SwipeablePhoto(photo = currentPhoto, onSwiped = { direction ->
            onPhotoSwiped(currentPhoto, direction)
            currentIndex = (currentIndex + 1) % photos.size
        })
    }
}

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun SwipeablePhoto(
    photo: File, onSwiped: (SwipeDirection) -> Unit
) {
    val swipeableState = rememberSwipeableState(initialValue = 0)
    val sizePx = with(LocalDensity.current) { 128.dp.toPx() }
    val anchors = mapOf(
        -sizePx to -1, 0f to 0, sizePx to 1
    )

    Log.e("EEEEE", photo.path)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Horizontal
            )
    ) {
        Image(
            painter = rememberAsyncImagePainter(photo),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }

    LaunchedEffect(swipeableState.currentValue) {
        when (swipeableState.currentValue) {
            -1 -> onSwiped(SwipeDirection.LEFT)
            1 -> onSwiped(SwipeDirection.RIGHT)
        }
    }
}

enum class SwipeDirection {
    LEFT, RIGHT
}