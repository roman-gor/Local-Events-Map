package com.gorman.featureauth.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gorman.featureauth.R
import com.gorman.featureauth.components.DefaultOutlinedTextField
import com.gorman.featureauth.components.PasswordTextField
import com.gorman.featureauth.states.AuthScreenUiEvent
import com.gorman.featureauth.viewmodels.AuthViewModel
import com.gorman.ui.theme.LocalEventsMapTheme

@Composable
fun SignInScreenEntry(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    SignInScreen(
        modifier = modifier,
        onUiEvent = authViewModel::onUiEvent,
        onNavigateToSignUp = {}
    )
}

@Composable
fun SignInScreen(
    onUiEvent: (AuthScreenUiEvent) -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToSignUp: () -> Unit
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
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
                    value = email.value,
                    onValueChange = { email.value = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    placeholder = stringResource(R.string.email),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                PasswordTextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    placeholder = stringResource(R.string.password),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth().height(55.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { onUiEvent(AuthScreenUiEvent.OnSignInClick(email.value, password.value)) },
                    modifier = Modifier.wrapContentWidth().height(55.dp)
                ) {
                    Text(
                        text = stringResource(R.string.signUp),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(LocalEventsMapTheme.dimens.paddingMedium)
                    )
                }
                IconButton(
                    onClick = { onNavigateToSignUp() },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.size(55.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
