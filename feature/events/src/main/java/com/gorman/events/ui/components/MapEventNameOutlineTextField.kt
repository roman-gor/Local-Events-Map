package com.gorman.events.ui.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.gorman.events.R
import com.gorman.ui.theme.LocalEventsMapTheme

@Composable
fun MapEventNameOutlineTextField(
    modifier: Modifier,
    currentName: String,
    onNameChanged: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    OutlinedTextField(
        value = currentName,
        onValueChange = {
            onNameChanged(it)
        },
        shape = LocalEventsMapTheme.shapes.extraLarge,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
            unfocusedContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
            errorContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
            disabledContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.8f)
        ),
        placeholder = {
            Text(
                text = stringResource(R.string.titleEvent),
                style = MaterialTheme.typography.bodyLarge
            )
        },
        textStyle = MaterialTheme.typography.bodyLarge,
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
            }
        ),
        modifier = modifier
    )
}
