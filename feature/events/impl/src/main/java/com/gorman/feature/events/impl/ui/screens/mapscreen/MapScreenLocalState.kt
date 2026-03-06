package com.gorman.feature.events.impl.ui.screens.mapscreen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberMapScreenLocalState(): MapScreenLocalState {
    val mapEventsListExpanded = remember { mutableStateOf(false) }
    val filtersExpanded = remember { mutableStateOf(false) }
    val citiesMenuExpanded = remember { mutableStateOf(false) }
    val isEventSelected = remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val mapEventsListSheetState = rememberModalBottomSheetState()
    val filtersSheetState = rememberModalBottomSheetState()

    return remember(scope, mapEventsListSheetState, filtersSheetState) {
        MapScreenLocalState(
            scope = scope,
            mapEventsListSheetState = mapEventsListSheetState,
            filtersSheetState = filtersSheetState,
            mapEventsListExpandedState = mapEventsListExpanded,
            filtersExpandedState = filtersExpanded,
            citiesMenuExpandedState = citiesMenuExpanded,
            isEventSelectedState = isEventSelected
        )
    }
}

@Immutable
@OptIn(ExperimentalMaterial3Api::class)
data class MapScreenLocalState(
    val scope: CoroutineScope,
    val mapEventsListSheetState: SheetState,
    val filtersSheetState: SheetState,
    private val isEventSelectedState: MutableState<Boolean>,
    private val mapEventsListExpandedState: MutableState<Boolean>,
    private val filtersExpandedState: MutableState<Boolean>,
    private val citiesMenuExpandedState: MutableState<Boolean>
) {
    var mapEventsListExpanded by mapEventsListExpandedState
    var filtersExpanded by filtersExpandedState
    var citiesMenuExpanded by citiesMenuExpandedState
}
