package com.gorman.events.ui.screens.mapscreen

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.gorman.events.ui.states.ScreenSideEffect
import com.gorman.events.ui.utils.MapController
import com.gorman.events.ui.viewmodels.MapViewModel

@Composable
fun HandleSideEffects(
    context: Context,
    mapViewModel: MapViewModel,
    mapController: MapController
) {
    LaunchedEffect(Unit) {
        mapViewModel.sideEffect.collect { effect ->
            when (effect) {
                is ScreenSideEffect.MoveCamera -> {
                    mapController.moveCamera(effect.point, effect.zoom)
                }
                is ScreenSideEffect.ShowToast -> {
                    Toast.makeText(context, effect.text, Toast.LENGTH_SHORT).show()
                }
                is ScreenSideEffect.OnNavigateToDetailsScreen -> {
                }
            }
        }
    }
}
