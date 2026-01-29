package com.gorman.feature.auth.impl.screens

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
import com.gorman.feature.auth.impl.R
import com.gorman.feature.auth.impl.components.FieldsBlockData
import com.gorman.feature.auth.impl.components.TextFieldsBlock
import com.gorman.feature.auth.impl.states.AuthScreenState
import com.gorman.feature.auth.impl.states.AuthScreenUiEvent
import com.gorman.feature.auth.impl.states.AuthSideEffects
import com.gorman.feature.auth.impl.states.UserUiState
import com.gorman.feature.auth.impl.viewmodels.AuthViewModel
import com.gorman.ui.components.LoadingStub
import com.gorman.ui.theme.LocalEventsMapTheme

@Composable
fun SignUpScreenEntry(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        authViewModel.sideEffect.collect { effect ->
            when (effect) {
                is AuthSideEffects.ShowToast -> {
                    Toast.makeText(context, effect.text, Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
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
                                UserUiState(email.value, username.value),
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
