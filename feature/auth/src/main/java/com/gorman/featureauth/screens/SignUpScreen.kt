package com.gorman.featureauth.screens

import android.widget.Toast
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gorman.featureauth.R
import com.gorman.featureauth.components.FieldsBlockData
import com.gorman.featureauth.components.TextFieldsBlock
import com.gorman.featureauth.states.AuthScreenState
import com.gorman.featureauth.states.AuthScreenUiEvent
import com.gorman.featureauth.states.UserUiState
import com.gorman.featureauth.viewmodels.AuthViewModel
import com.gorman.ui.components.LoadingStub
import com.gorman.ui.theme.LocalEventsMapTheme

@Composable
fun SignUpScreenEntry(
    onNavigateToMain: (UserUiState) -> Unit,
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (uiState is AuthScreenState.Error) {
            val error = (uiState as AuthScreenState.Error).e
            Toast.makeText(context, error.localizedMessage ?: "Ошибка", Toast.LENGTH_LONG).show()
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ) {
        SignUpScreen(
            modifier = Modifier.fillMaxWidth(),
            onUiEvent = authViewModel::onUiEvent
        )
        if (uiState is AuthScreenState.Loading) {
            LoadingStub()
        }

        if (uiState is AuthScreenState.Success) {
            LaunchedEffect(Unit) {
                onNavigateToMain((uiState as AuthScreenState.Success).userData)
            }
        }
    }
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
    Column(
        modifier = modifier,
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
                if (password.value == repeatPassword.value && password.value.isNotEmpty() &&
                    repeatPassword.value.isNotEmpty()
                ) {
                    if (email.value.isNotEmpty() && username.value.isNotEmpty()) {
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
