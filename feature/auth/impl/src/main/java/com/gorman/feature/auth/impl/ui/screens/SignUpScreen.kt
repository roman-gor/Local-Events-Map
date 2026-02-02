package com.gorman.feature.auth.impl.ui.screens

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.gorman.feature.auth.impl.R
import com.gorman.feature.auth.impl.ui.components.FieldsBlockData
import com.gorman.feature.auth.impl.ui.components.TextFieldsBlock
import com.gorman.feature.auth.impl.ui.states.AuthScreenState
import com.gorman.feature.auth.impl.ui.states.AuthScreenUiEvent
import com.gorman.feature.auth.impl.ui.states.AuthSideEffects
import com.gorman.feature.auth.impl.ui.utils.isEmailValid
import com.gorman.feature.auth.impl.ui.utils.isPasswordValid
import com.gorman.feature.auth.impl.ui.viewmodels.AuthViewModel
import com.gorman.ui.components.LoadingStub
import com.gorman.ui.states.UserUiState
import com.gorman.ui.theme.LocalEventsMapTheme

@Composable
fun SignUpScreenEntry(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()

    val (networkErrorText, incorrectDataText, emailInUseError) = listOf(
        stringResource(R.string.networkErrorText),
        stringResource(R.string.incorrectUserData),
        stringResource(R.string.emailInUseError)
    )

    LaunchedEffect(Unit) {
        authViewModel.sideEffect.collect { effect ->
            when (effect) {
                is AuthSideEffects.ShowToast -> {
                    Toast.makeText(context, effect.text, Toast.LENGTH_LONG).show()
                }
                is AuthSideEffects.ShowError -> {
                    when (effect.e) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            authViewModel.onUiEvent(AuthScreenUiEvent.ShowToast(incorrectDataText))
                        }
                        is FirebaseNetworkException -> {
                            authViewModel.onUiEvent(AuthScreenUiEvent.ShowToast(networkErrorText))
                        }
                        is FirebaseAuthUserCollisionException -> {
                            authViewModel.onUiEvent(AuthScreenUiEvent.ShowToast(emailInUseError))
                        }
                    }
                }
            }
        }
    }

    when (val state = uiState) {
        AuthScreenState.Loading -> LoadingStub()
        is AuthScreenState.Idle -> {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.TopCenter
            ) {
                SignUpScreen(
                    uiState = state,
                    modifier = Modifier.fillMaxWidth(),
                    onUiEvent = authViewModel::onUiEvent
                )
            }
        }
        else -> {}
    }
}

@Composable
fun SignUpScreen(
    uiState: AuthScreenState.Idle,
    modifier: Modifier = Modifier,
    onUiEvent: (AuthScreenUiEvent) -> Unit
) {
    val email = uiState.user.email ?: ""
    val password = uiState.password
    var repeatPassword by remember { mutableStateOf("") }
    val username = uiState.user.username ?: ""
    val incorrectEmailText = stringResource(R.string.incorrectEmail)
    val incorrectPasswordText = stringResource(R.string.incorrectPassword)
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
                email = email,
                password = password,
                repeatPassword = repeatPassword,
                username = username,
                onChangeEmail = { onUiEvent(AuthScreenUiEvent.OnEmailChange(it)) },
                onChangePassword = { onUiEvent(AuthScreenUiEvent.OnPasswordChange(it)) },
                onChangeRepeatPassword = { repeatPassword = it },
                onChangeUsername = { onUiEvent(AuthScreenUiEvent.OnUsernameChange(it)) }
            )
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                if (isEmailValid(email) && isPasswordValid(password) &&
                    repeatPassword.isNotEmpty()
                ) {
                    if (username.isNotEmpty() && password == repeatPassword) {
                        onUiEvent(
                            AuthScreenUiEvent.OnSignUpClick(
                                UserUiState(email = email, username = username),
                                password
                            )
                        )
                    }
                } else if (!isEmailValid(email)) {
                    onUiEvent(AuthScreenUiEvent.ShowToast(incorrectEmailText))
                } else {
                    onUiEvent(AuthScreenUiEvent.ShowToast(incorrectPasswordText))
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
