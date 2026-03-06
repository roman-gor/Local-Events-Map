package com.gorman.feature.bookmarks.impl.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.gorman.feature.bookmarks.impl.R
import com.gorman.ui.states.MapUiEvent
import com.gorman.ui.theme.LocalEventsMapTheme
import com.gorman.ui.utils.getBottomBarPadding
import kotlinx.collections.immutable.ImmutableList

@Composable
fun BookmarkList(
    events: ImmutableList<MapUiEvent>,
    onEventClick: (String) -> Unit,
    onLikeButtonClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        contentPadding = PaddingValues(
            bottom = getBottomBarPadding() + LocalEventsMapTheme.dimens.paddingMedium
        )
    ) {
        items(
            items = events,
            key = { event -> event.id }
        ) { event ->
            BookmarkEventCard(
                event = event,
                onLikeButtonClick = { onLikeButtonClick(event.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(LocalEventsMapTheme.dimens.paddingMedium)
                    .clip(LocalEventsMapTheme.shapes.large)
                    .clickable(onClick = {
                        onEventClick(event.id)
                    })
            )
        }
    }
}

@Composable
fun BookmarkEventCard(
    event: MapUiEvent,
    onLikeButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLike = event.isFavourite ?: true
    Box(
        modifier = modifier
    ) {
        AsyncImage(
            imageUrl = event.photoUrl,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(MaterialTheme.colorScheme.onPrimary)
        )
        IconButton(
            onClick = { onLikeButtonClick() },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = if (isLike) {
                    Icons.Filled.Favorite
                } else {
                    Icons.Filled.FavoriteBorder
                },
                contentDescription = "Like Button Icon",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .size(42.dp)
                    .padding(
                        top = LocalEventsMapTheme.dimens.paddingMedium,
                        end = LocalEventsMapTheme.dimens.paddingMedium
                    )
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f))
                .padding(vertical = LocalEventsMapTheme.dimens.paddingMedium),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = event.name.orEmpty(),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(horizontal = LocalEventsMapTheme.dimens.paddingMedium)
                )
                Text(
                    text = event.dateDisplay.orEmpty(),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(horizontal = LocalEventsMapTheme.dimens.paddingMedium)
                )
            }
        }
    }
}

@Composable
fun AsyncImage(
    imageUrl: String?,
    modifier: Modifier = Modifier
) {
    SubcomposeAsyncImage(
        model = imageUrl,
        contentDescription = "Event Image with composable placeholder",
        modifier = modifier,
        contentScale = ContentScale.Crop,
        loading = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        },
        error = {
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(MaterialTheme.colorScheme.onPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_placeholder),
                    contentDescription = "Image Placeholder",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    )
}
