package com.saventiy.cullpix.views

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import coil.compose.rememberAsyncImagePainter
import com.saventiy.cullpix.repository.impl.Photo

@Composable
fun PhotoManagerScreen(viewModel: PhotoViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.deletionPendingIntent) {
        state.deletionPendingIntent?.let { pendingIntent ->
            // You would typically start this PendingIntent,
            // but that requires an Activity context
            // This is just a placeholder to show where you'd handle it
            Log.d("PhotoManagerScreen", "Deletion requires permission")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.photos.isEmpty()) {
            Text(
                "No photos found",
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            PhotoSwiper(
                photos = state.photos,
                onPhotoSwiped = { photo, direction ->
                    when (direction) {
                        SwipeDirection.LEFT -> viewModel.onIntent(PhotoIntent.DeletePhoto(photo))
                        SwipeDirection.RIGHT -> viewModel.onIntent(PhotoIntent.KeepPhoto(photo))
                    }
                }
            )
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
    photos: List<Photo>,
    onPhotoSwiped: (Photo, SwipeDirection) -> Unit
) {
    var currentIndex by remember { mutableStateOf(0) }
    val currentPhoto = photos.getOrNull(currentIndex)

    if (currentPhoto != null) {
        SwipeablePhoto(
            photo = currentPhoto,
            onSwiped = { direction ->
                onPhotoSwiped(currentPhoto, direction)
                currentIndex = (currentIndex + 1) % photos.size
            }
        )
    }
}

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun SwipeablePhoto(
    photo: Photo,
    onSwiped: (SwipeDirection) -> Unit
) {
    val swipeableState = rememberSwipeableState(initialValue = 0)
    val sizePx = with(LocalDensity.current) { 128.dp.toPx() }
    val anchors = mapOf(
        -sizePx to -1,
        0f to 0,
        sizePx to 1
    )

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
            painter = rememberAsyncImagePainter(photo.uri),
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