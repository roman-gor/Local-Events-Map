package com.gorman.detailsevent.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.gorman.common.constants.categoryNameDefinition
import com.gorman.common.constants.cityNameDefinition

@Composable
fun BottomBlock(
    cityName: String,
    category: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = cityNameDefinition(cityName),
            style = MaterialTheme.typography.titleMedium,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "#${categoryNameDefinition(category)}",
            style = MaterialTheme.typography.titleMedium,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
