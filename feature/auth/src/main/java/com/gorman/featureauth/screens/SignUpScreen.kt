package com.gorman.featureauth.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gorman.featureauth.R
import com.gorman.featureauth.components.FieldsBlockData
import com.gorman.featureauth.components.TextFieldsBlock
import com.gorman.featureauth.states.AuthScreenUiEvent
import com.gorman.featureauth.states.UserUiState
import com.gorman.featureauth.viewmodels.AuthViewModel
import com.gorman.ui.theme.LocalEventsMapTheme

@Composable
fun SignUpScreenEntry(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    SignUpScreen(
        modifier = modifier,
        onUiEvent = authViewModel::onUiEvent
    )
}

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    onUiEvent: (AuthScreenUiEvent) -> Unit
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val repeatPassword = remember { mutableStateOf("") }
    val username = remember { mutableStateOf("") }
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(R.string.signUp),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.weight(1f))
            TextFieldsBlock(
                modifier = Modifier.fillMaxWidth(),
                fieldsBlockData = FieldsBlockData(
                    email = email.value,
                    password = password.value,
                    repeatPassword = repeatPassword.value,
                    username = username.value,
                    onChangeEmail = { email.value = it },
                    onChangePassword = { password.value = it },
                    onChangeRepeatPassword = { repeatPassword.value = it },
                    onChangeUsername = { username.value = it }
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    if (password.value == repeatPassword.value) {
                        onUiEvent(
                            AuthScreenUiEvent.OnSignUpClick(
                                UserUiState(
                                    email = email.value,
                                    username = username.value
                                ),
                                password.value
                            )
                        )
                    }
                },
                modifier = Modifier.wrapContentWidth()
            ) {
                Text(
                    text = stringResource(R.string.signUp),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(LocalEventsMapTheme.dimens.paddingMedium)
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
