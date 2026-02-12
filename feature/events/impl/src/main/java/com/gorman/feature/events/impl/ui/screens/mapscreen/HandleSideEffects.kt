package com.gorman.feature.events.impl.ui.screens.mapscreen

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.gorman.feature.events.impl.ui.mappers.toDomain
import com.gorman.feature.events.impl.ui.states.ScreenSideEffect
import com.gorman.feature.events.impl.ui.viewmodels.MapViewModel
import com.gorman.map.ui.MapControl

@Composable
fun HandleSideEffects(
    context: Context,
    mapViewModel: MapViewModel,
    mapControl: MapControl
) {
    LaunchedEffect(mapViewModel.sideEffect) {
        mapViewModel.sideEffect.collect { effect ->
            when (effect) {
                is ScreenSideEffect.MoveCamera -> {
                    val zoom = effect.zoom
                    mapControl.moveCamera(
                        point = effect.point.toDomain(),
                        zoom = zoom
                    )
                }
                is ScreenSideEffect.ShowToast -> {
                    Toast.makeText(context, effect.text, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
