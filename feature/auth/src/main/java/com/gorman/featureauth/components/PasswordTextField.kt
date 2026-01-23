package com.gorman.featureauth.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.gorman.featureauth.R
import com.gorman.ui.theme.LocalEventsMapTheme

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null
) {
    val showPassword = remember { mutableStateOf(false) }
    val passwordVisualTransformation = remember { PasswordVisualTransformation() }
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
        visualTransformation = if (showPassword.value) {
            VisualTransformation.None
        } else {
            passwordVisualTransformation
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = modifier,
        trailingIcon = {
            Icon(
                if (showPassword.value) {
                    painterResource(R.drawable.ic_visibility)
                } else {
                    painterResource(R.drawable.ic_visibility_off)
                },
                contentDescription = "Toggle password visibility",
                modifier = Modifier.clickable { showPassword.value = !showPassword.value }
            )
        },
        singleLine = true
    )
}
