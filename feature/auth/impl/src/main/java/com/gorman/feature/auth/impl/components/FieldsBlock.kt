package com.gorman.featureauth.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gorman.featureauth.R

@Composable
fun TextFieldsBlock(
    fieldsBlockData: FieldsBlockData,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
    ) {
        DefaultOutlinedTextField(
            value = fieldsBlockData.email,
            onValueChange = { fieldsBlockData.onChangeEmail(it) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            placeholder = stringResource(R.string.email),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        PasswordTextField(
            value = fieldsBlockData.password,
            onValueChange = { fieldsBlockData.onChangePassword(it) },
            placeholder = stringResource(R.string.password),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        PasswordTextField(
            value = fieldsBlockData.repeatPassword,
            onValueChange = { fieldsBlockData.onChangeRepeatPassword(it) },
            placeholder = stringResource(R.string.repeatPassword),
            modifier = Modifier.fillMaxWidth()
        )
        if (
            fieldsBlockData.repeatPassword != fieldsBlockData.password &&
            fieldsBlockData.repeatPassword.isNotEmpty()
        ) {
            Text(
                text = stringResource(R.string.notMatchPasswords),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        DefaultOutlinedTextField(
            value = fieldsBlockData.username,
            onValueChange = { fieldsBlockData.onChangeUsername(it) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            placeholder = stringResource(R.string.username),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Immutable
data class FieldsBlockData(
    val email: String,
    val password: String,
    val repeatPassword: String,
    val username: String,
    val onChangeEmail: (String) -> Unit,
    val onChangePassword: (String) -> Unit,
    val onChangeRepeatPassword: (String) -> Unit,
    val onChangeUsername: (String) -> Unit
)
