package com.gorman.feature.events.impl.ui.screens.mapscreen

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gorman.common.constants.Category
import com.gorman.common.constants.CostTier
import com.gorman.common.models.DateFilterType
import com.gorman.common.models.FilterActions
import com.gorman.common.models.FilterOptions
import com.gorman.common.models.FiltersState
import com.gorman.feature.events.impl.ui.components.DateRangePickerDialog
import com.gorman.feature.events.impl.ui.components.FiltersBottomSheet
import com.gorman.feature.events.impl.ui.components.MapEventsBottomSheet
import com.gorman.feature.events.impl.ui.states.MapScreenActions
import com.gorman.ui.states.MapUiEvent
import com.gorman.ui.theme.LocalEventsMapTheme
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapEventsBottomSheetContent(
    data: BottomSheetData,
    onEventClick: (MapUiEvent) -> Unit,
    eventsList: ImmutableList<MapUiEvent>,
    modifier: Modifier = Modifier
) {
    if (data.expanded) {
        MapEventsBottomSheet(
            onDismiss = { data.onDismissSheet() },
            onEventClick = { onEventClick(it) },
            eventsList = eventsList,
            sheetState = data.sheetState,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheetContent(
    data: BottomSheetData,
    filtersState: FiltersState,
    mapScreenActions: MapScreenActions,
    modifier: Modifier = Modifier
) {
    val showRangePicker = remember { mutableStateOf(false) }
    if (data.expanded) {
        FiltersBottomSheet(
            onDismiss = { data.onDismissSheet() },
            sheetState = data.sheetState,
            filters = filtersState,
            options = FilterOptions(
                categoryItems = Category.entries,
                costItems = CostTier.entries.map { it.value }
            ),
            actions = FilterActions(
                onCategoryChange = mapScreenActions.filterActions.onCategoryChange,
                onDateRangeChange = { dateState ->
                    Log.d("Date", dateState.toString())
                    if (dateState.type == DateFilterType.RANGE) {
                        showRangePicker.value = true
                    } else {
                        mapScreenActions.filterActions.onDateRangeChange(dateState)
                    }
                },
                onDistanceChange = { mapScreenActions.filterActions.onDistanceChange(it) },
                onCostChange = { mapScreenActions.filterActions.onCostChange(it) },
                onNameChange = { name -> mapScreenActions.filterActions.onNameChange(name) },
                onResetFilters = { mapScreenActions.filterActions.onResetFilters() }
            ),
            modifier = modifier
        )
    }
    if (showRangePicker.value) {
        DateRangePickerDialog(
            onDateRangeSelected = { dateState ->
                mapScreenActions.filterActions.onDateRangeChange(dateState)
            },
            onDismiss = { showRangePicker.value = !showRangePicker.value },
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(
                    vertical = LocalEventsMapTheme.dimens.paddingLarge
                )
        )
    }
}

@Immutable
data class BottomSheetData
@OptIn(ExperimentalMaterial3Api::class)
constructor(
    val expanded: Boolean,
    val onDismissSheet: () -> Unit,
    val sheetState: SheetState
)
