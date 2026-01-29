package com.gorman.feature.events.impl.screens.mapscreen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gorman.feature.events.impl.R
import com.gorman.feature.events.impl.components.FunctionalButton
import com.gorman.feature.events.impl.components.MapEventNameOutlineTextField
import com.gorman.feature.events.impl.components.MapEventSelectedButton
import com.gorman.feature.events.impl.states.MapScreenActions
import com.gorman.ui.states.MapUiEvent
import com.gorman.ui.theme.LocalEventsMapTheme

@SuppressLint("ComposeModifierMissing")
@Composable
fun BoxScope.FunctionalBlock(
    mapScreenData: MapScreenData
) {
    FunctionalButton(
        onClick = { mapScreenData.mapScreenActions.onSyncClick() },
        iconSize = 32.dp,
        imageVector = Icons.Outlined.Refresh,
        modifier = Modifier
            .padding(LocalEventsMapTheme.dimens.paddingExtraLarge)
            .size(48.dp)
            .align(alignment = Alignment.CenterEnd)
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(LocalEventsMapTheme.dimens.paddingExtraLarge)
            .align(Alignment.BottomCenter),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FunctionalButton(
                onClick = { mapScreenData.onMapEventsListExpanded() },
                iconSize = 32.dp,
                imageVector = Icons.Outlined.Menu,
                modifier = Modifier
                    .size(48.dp)
                    .offset(y = mapScreenData.listEventsButtonVerticalOffset)
            )
            FunctionalButton(
                onClick = { mapScreenData.onFiltersExpanded() },
                iconSize = 32.dp,
                painter = painterResource(R.drawable.filter_alt),
                modifier = Modifier
                    .size(48.dp)
                    .offset(y = mapScreenData.filtersButtonVerticalOffset)
            )
        }
        Spacer(modifier = Modifier.height(LocalEventsMapTheme.dimens.paddingLarge))
        MapEventNameOutlineTextField(
            modifier = Modifier.fillMaxWidth(),
            currentName = mapScreenData.name,
            onNameChanged = { mapScreenData.mapScreenActions.filterActions.onNameChange(it) }
        )
        if (mapScreenData.isEventSelected) {
            mapScreenData.selectedEvent?.let { event ->
                MapEventSelectedButton(
                    onMapEventButtonClick = { mapScreenData.onMapEventSelectedItemClick(event) },
                    mapEvent = event,
                    modifier = Modifier.fillMaxWidth()
                        .padding(vertical = LocalEventsMapTheme.dimens.paddingLarge)
                )
            }
        }
    }
}

@Immutable
data class MapScreenData(
    val name: String,
    val selectedEvent: MapUiEvent? = null,
    val listEventsButtonVerticalOffset: Dp,
    val filtersButtonVerticalOffset: Dp,
    val mapScreenActions: MapScreenActions,
    val isEventSelected: Boolean,
    val onMapEventsListExpanded: () -> Unit,
    val onFiltersExpanded: () -> Unit,
    val onMapEventSelectedItemClick: (MapUiEvent) -> Unit
)
