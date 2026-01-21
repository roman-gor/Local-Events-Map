package com.gorman.events.ui.screens.mapscreen

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.gorman.common.constants.CategoryConstants.Companion.categoriesList
import com.gorman.common.constants.CostConstants
import com.gorman.events.ui.components.DateFilterType
import com.gorman.events.ui.components.DateRangePickerDialog
import com.gorman.events.ui.components.FiltersBottomSheet
import com.gorman.events.ui.components.MapEventsBottomSheet
import com.gorman.events.ui.states.FilterActions
import com.gorman.events.ui.states.FilterOptions
import com.gorman.events.ui.states.FiltersState
import com.gorman.events.ui.states.MapScreenActions
import com.gorman.events.ui.states.MapUiEvent
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapEventsBottomSheetContent(
    data: BottomSheetData,
    onEventClick: (MapUiEvent) -> Unit,
    eventsList: ImmutableList<MapUiEvent>
) {
    if (data.expanded) {
        MapEventsBottomSheet(
            onDismiss = { data.onDismissSheet() },
            onEventClick = { onEventClick(it) },
            eventsList = eventsList,
            sheetState = data.sheetState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheetContent(
    data: BottomSheetData,
    filtersState: FiltersState,
    mapScreenActions: MapScreenActions
) {
    val showRangePicker = remember { mutableStateOf(false) }
    if (data.expanded) {
        FiltersBottomSheet(
            onDismiss = { data.onDismissSheet() },
            sheetState = data.sheetState,
            filters = filtersState,
            options = FilterOptions(
                categoryItems = categoriesList,
                costItems = CostConstants.costList.map { it.value }
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
                onNameChange = { name ->
                    mapScreenActions.filterActions.onNameChange(name)
                }
            )
        )
    }
    if (showRangePicker.value) {
        DateRangePickerDialog(
            onDateRangeSelected = { dateState ->
                mapScreenActions.filterActions.onDateRangeChange(dateState)
            },
            onDismiss = { showRangePicker.value = !showRangePicker.value }
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
