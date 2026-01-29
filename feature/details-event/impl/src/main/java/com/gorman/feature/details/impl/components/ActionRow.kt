package com.gorman.feature.details.impl.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gorman.feature.details.impl.R
import com.gorman.feature.details.impl.states.DetailsActions
import com.gorman.ui.theme.LocalEventsMapTheme

@Composable
fun ActionsRow(
    selectedIcon: ImageVector,
    detailsActions: DetailsActions,
    modifier: Modifier = Modifier
) {
    val iconSize = 36.dp
    Card(
        modifier = modifier,
        shape = LocalEventsMapTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = LocalEventsMapTheme.dimens.paddingMedium,
                    horizontal = LocalEventsMapTheme.dimens.paddingLarge
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = { detailsActions.onFavouriteClick() }) {
                Icon(
                    imageVector = selectedIcon,
                    contentDescription = "Heart Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(iconSize)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { detailsActions.onLocationClick() }) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location Marker Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(iconSize)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(onClick = { detailsActions.onShareClick() }) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(iconSize)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(onClick = { detailsActions.onLinkClick() }) {
                Icon(
                    painter = painterResource(R.drawable.ic_link),
                    contentDescription = "Link Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(iconSize)
                )
            }
        }
    }
}
