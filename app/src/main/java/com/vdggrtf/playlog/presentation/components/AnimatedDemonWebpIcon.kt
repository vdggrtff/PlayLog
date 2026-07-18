package com.vdggrtf.playlog.presentation.components

import android.os.Build.VERSION.SDK_INT
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.vdggrtf.playlog.R

@Composable
fun AnimatedDemonWebpIcon(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // Create smart downloader
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                if (SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory()) //For new
                } else {
                    add(GifDecoder.Factory()) // For old
                }
            }
            .build()
    }

    AsyncImage(
        model = R.drawable.demon_animate_mvp,
        imageLoader = imageLoader,
        contentDescription = "Animated Demon",
        modifier = modifier
    )
}