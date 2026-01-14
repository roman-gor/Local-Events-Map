package com.gorman.events.ui.permissions

import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.isGranted

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissionsHandler(
    locationPermissionsState: MultiplePermissionsState,
    allPermissionsGranted: @Composable () -> Unit,
    shouldShowRationale: @Composable () -> Unit,
    isPermanentlyDeclined: @Composable () -> Unit,
    requestPermissions: @Composable () -> Unit
) {
    when {
        locationPermissionsState.allPermissionsGranted -> {
            allPermissionsGranted()
        }
        locationPermissionsState.shouldShowRationale -> {
            shouldShowRationale()
        }
        !locationPermissionsState.allPermissionsGranted && !locationPermissionsState.shouldShowRationale -> {
            val isFirstRequest = locationPermissionsState.revokedPermissions.all { it.status.isGranted }
            if (isFirstRequest) {
                requestPermissions()
            } else {
                isPermanentlyDeclined()
            }
        }
        else -> {
            requestPermissions()
        }
    }
}
