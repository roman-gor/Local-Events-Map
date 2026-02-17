package com.gorman.feature.events.impl.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gorman.ui.states.MapUiEvent
import com.gorman.ui.theme.LocalEventsMapTheme
import com.gorman.ui.utils.DateFormatStyle
import com.gorman.ui.utils.format

@Composable
fun MapEventSelectedButton(
    onMapEventButtonClick: (MapUiEvent) -> Unit,
    mapEvent: MapUiEvent,
    modifier: Modifier = Modifier
) {
    val date = mapEvent.date?.format(DateFormatStyle.DATE_ONLY)
    Button(
        onClick = { onMapEventButtonClick(mapEvent) },
        modifier = modifier,
        shape = LocalEventsMapTheme.shapes.large,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = LocalEventsMapTheme.dimens.paddingLarge),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                mapEvent.name?.let {
                    Text(
                        text = it,
                        fontSize = 22.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                mapEvent.description?.let {
                    Text(
                        text = it,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = date.orEmpty(),
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                textAlign = TextAlign.End
            )
        }
    }
}
