package com.gorman.feature.details.impl.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gorman.ui.states.DateEventUiModel

@Composable
fun DateTimeSection(
    dateUi: DateEventUiModel,
    onCalendarClick: () -> Unit,
    onLocationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Column {
            Text(
                text = dateUi.day,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = dateUi.month,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.width(32.dp))
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = dateUi.dayOfWeek.replaceFirstChar { it.titlecase() },
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = dateUi.time,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 14.sp
                )
            }
            Row(
                modifier = Modifier.wrapContentWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassyButton(
                    icon = Icons.Outlined.DateRange,
                    contentDescription = "Calendar Icon",
                    onClick = onCalendarClick,
                    modifier = Modifier.size(48.dp),
                    iconModifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                GlassyButton(
                    icon = Icons.Outlined.LocationOn,
                    contentDescription = "Location",
                    onClick = onLocationClick,
                    modifier = Modifier.size(48.dp),
                    iconModifier = Modifier.size(30.dp)
                )
            }
        }
    }
}
