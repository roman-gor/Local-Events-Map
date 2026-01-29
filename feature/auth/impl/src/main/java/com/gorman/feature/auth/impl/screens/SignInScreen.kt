package com.gorman.feature.auth.impl.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.gorman.feature.auth.impl.R
import com.gorman.feature.auth.impl.components.BottomButtons
import com.gorman.feature.auth.impl.components.DefaultOutlinedTextField
import com.gorman.feature.auth.impl.components.PasswordTextField
import com.gorman.feature.auth.impl.states.AuthScreenState
import com.gorman.feature.auth.impl.states.AuthScreenUiEvent
import com.gorman.feature.auth.impl.states.AuthSideEffects
import com.gorman.feature.auth.impl.utils.isEmailValid
import com.gorman.feature.auth.impl.utils.isPasswordValid
import com.gorman.feature.auth.impl.viewmodels.AuthViewModel
import com.gorman.ui.components.LoadingStub

@Composable
fun SignInScreenEntry(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()

    val (networkErrorText, incorrectDataText) = Pair(
        stringResource(R.string.networkErrorText),
        stringResource(R.string.incorrectUserData)
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
                SignInScreen(
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
fun SignInScreen(
    uiState: AuthScreenState.Idle,
    onUiEvent: (AuthScreenUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val email = uiState.user.email ?: ""
    val password = uiState.password
    val incorrectEmailText = stringResource(R.string.incorrectEmail)
    val incorrectPasswordText = stringResource(R.string.incorrectPassword)
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(R.string.signIn),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.weight(1f))
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            DefaultOutlinedTextField(
                value = email,
                onValueChange = { onUiEvent(AuthScreenUiEvent.OnEmailChange(it)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                placeholder = stringResource(R.string.email),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            PasswordTextField(
                value = password,
                onValueChange = { onUiEvent(AuthScreenUiEvent.OnPasswordChange(it)) },
                placeholder = stringResource(R.string.password),
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        BottomButtons(
            onNavigateToSignUp = { onUiEvent(AuthScreenUiEvent.OnNavigateToSignUpClicked) },
            onSignInClick = {
                if (isEmailValid(email) && isPasswordValid(password)) {
                    onUiEvent(AuthScreenUiEvent.OnSignInClick(email, password))
                } else if (!isEmailValid(email)) {
                    onUiEvent(AuthScreenUiEvent.ShowToast(incorrectEmailText))
                } else {
                    onUiEvent(AuthScreenUiEvent.ShowToast(incorrectPasswordText))
                }
            },
            modifier = Modifier.fillMaxWidth().height(55.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(
            onClick = { onUiEvent(AuthScreenUiEvent.OnGuestSignIn) }
        ) {
            Text(
                text = stringResource(R.string.guestSignIn),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}
