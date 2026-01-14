package com.gorman.events.ui.permissions

import android.Manifest
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissionsHandler(
    allPermissionsGranted: () -> Unit,
    shouldShowRationale: () -> Unit,
    requestPermissions: () -> Unit
) {
    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )
    )
    when {
        locationPermissionsState.allPermissionsGranted -> {
            allPermissionsGranted()
        }
        locationPermissionsState.shouldShowRationale -> {
            shouldShowRationale()
        }
        else -> {
            requestPermissions()
        }
    }
}
