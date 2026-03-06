package com.gorman.feature.events.impl.ui.components

import android.icu.util.MeasureUnit
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.gorman.feature.events.impl.R

@Composable
fun DistanceSlider(
    distance: Int,
    enabled: Boolean,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val units = MeasureUnit.KILOMETER
    val unitText = when (units) {
        MeasureUnit.KILOMETER -> stringResource(R.string.km)
        else -> stringResource(R.string.km)
    }
    Column(
        modifier = modifier
    ) {
        Text(
            text = "$distance $unitText",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Slider(
            value = distance.toFloat(),
            onValueChange = {
                onValueChange(it)
            },
            onValueChangeFinished = {
                onValueChangeFinished()
            },
            enabled = enabled,
            valueRange = 1f..20f,
            steps = 20
        )
    }
}
