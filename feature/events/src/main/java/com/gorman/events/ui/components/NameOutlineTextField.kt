package com.gorman.events.ui.components

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.gorman.events.R
import com.gorman.ui.theme.LocalEventsMapTheme

@Composable
fun NameOutlinedTextField(
    modifier: Modifier,
    currentName: String,
    onNameChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = currentName,
        onValueChange = {
            onNameChanged(it)
        },
        shape = LocalEventsMapTheme.shapes.medium,
        placeholder = {
            Text(
                text = stringResource(R.string.titleEvent)
            )
        },
        modifier = modifier
    )
}
