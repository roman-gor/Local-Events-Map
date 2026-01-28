package com.gorman.feature.events.impl.screens.mapscreen

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberMapScreenLocalState(): MapScreenLocalState {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    val mapEventsListExpanded = remember { mutableStateOf(false) }
    val filtersExpanded = remember { mutableStateOf(false) }
    val citiesMenuExpanded = remember { mutableStateOf(false) }
    val isEventSelected = remember { mutableStateOf(false) }

    val isDarkMode = isSystemInDarkTheme()
    val scope = rememberCoroutineScope()

    val mapEventsListSheetState = rememberModalBottomSheetState()
    val filtersSheetState = rememberModalBottomSheetState()

    val heightPercentage = 0.5f

    val filtersButtonVerticalOffset = animateDpAsState(
        targetValue = if (filtersExpanded.value) -(screenHeight * heightPercentage) else 0.dp,
        animationSpec = tween(durationMillis = 500),
        label = "filtersOffset"
    )

    val listEventsButtonVerticalOffset = animateDpAsState(
        targetValue = if (mapEventsListExpanded.value) -(screenHeight * heightPercentage) else 0.dp,
        animationSpec = tween(durationMillis = 500),
        label = "listOffset"
    )

    return remember(isDarkMode, scope, mapEventsListSheetState, filtersSheetState) {
        MapScreenLocalState(
            isDarkMode = isDarkMode,
            scope = scope,
            mapEventsListSheetState = mapEventsListSheetState,
            filtersSheetState = filtersSheetState,
            filtersButtonOffset = filtersButtonVerticalOffset,
            listEventsButtonOffset = listEventsButtonVerticalOffset,
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
    val isDarkMode: Boolean,
    val scope: CoroutineScope,
    val mapEventsListSheetState: SheetState,
    val filtersSheetState: SheetState,
    val filtersButtonOffset: State<Dp>,
    val listEventsButtonOffset: State<Dp>,
    private val isEventSelectedState: MutableState<Boolean>,
    private val mapEventsListExpandedState: MutableState<Boolean>,
    private val filtersExpandedState: MutableState<Boolean>,
    private val citiesMenuExpandedState: MutableState<Boolean>
) {
    var mapEventsListExpanded by mapEventsListExpandedState
    var filtersExpanded by filtersExpandedState
    var citiesMenuExpanded by citiesMenuExpandedState
    var isEventSelected by isEventSelectedState
}
