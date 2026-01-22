package com.gorman.detailsevent.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImagePainter
import com.gorman.detailsevent.R

@Composable
fun AsyncPosterImage(
    state: AsyncImagePainter.State,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            is AsyncImagePainter.State.Empty -> {}
            is AsyncImagePainter.State.Error -> {
                Image(
                    painter = painterResource(R.drawable.ic_placeholder),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = Color.White)
                )
            }
            is AsyncImagePainter.State.Loading -> CircularProgressIndicator()
            is AsyncImagePainter.State.Success -> {
                Image(
                    painter = state.painter,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}
