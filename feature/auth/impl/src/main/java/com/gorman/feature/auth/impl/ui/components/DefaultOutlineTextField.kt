package com.gorman.feature.auth.impl.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.gorman.ui.theme.LocalEventsMapTheme

@Composable
fun DefaultOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions,
    modifier: Modifier = Modifier,
    placeholder: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        textStyle = MaterialTheme.typography.bodyMedium,
        placeholder = {
            if (placeholder != null) {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        shape = LocalEventsMapTheme.shapes.medium,
        keyboardOptions = keyboardOptions,
        modifier = modifier,
        singleLine = true
    )
}
