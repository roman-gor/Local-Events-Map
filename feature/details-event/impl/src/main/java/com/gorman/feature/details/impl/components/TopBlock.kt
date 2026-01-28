package com.gorman.feature.details.impl.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImagePainter
import com.gorman.feature.details.impl.states.DetailsActions
import com.gorman.ui.theme.LocalEventsMapTheme

@Composable
fun TopBlock(
    name: String?,
    isFavourite: Boolean?,
    imageState: AsyncImagePainter.State,
    detailsActions: DetailsActions,
    modifier: Modifier = Modifier
) {
    val selectedIcon = isFavourite?.let {
        if (it) {
            Icons.Default.Favorite
        } else {
            Icons.Default.FavoriteBorder
        }
    } ?: Icons.Default.FavoriteBorder
    Column(
        modifier = modifier
    ) {
        Text(
            text = name ?: "",
            style = MaterialTheme.typography.titleLarge,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))
        AsyncPosterImage(
            state = imageState,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(MaterialTheme.colorScheme.primary)
        )
        ActionsRow(
            selectedIcon = selectedIcon,
            detailsActions = DetailsActions(
                onFavouriteClick = detailsActions.onFavouriteClick,
                onLocationClick = detailsActions.onLocationClick,
                onShareClick = detailsActions.onShareClick,
                onLinkClick = detailsActions.onLinkClick
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(LocalEventsMapTheme.dimens.paddingLarge)
        )
    }
}
