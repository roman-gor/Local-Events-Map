package com.gorman.feature.bookmarks.impl.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gorman.feature.bookmarks.impl.R
import com.gorman.ui.states.UserUiState

@Composable
fun UserDataCard(
    user: UserUiState,
    onSignOutClick: () -> Unit,
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        if (!user.email.isNullOrEmpty()) {
            UserCard(
                user = user,
                onSignOutClick = onSignOutClick
            )
        } else {
            SignUpCard(
                onSignUpClick = onSignUpClick
            )
        }
    }
}

@Composable
fun UserCard(
    user: UserUiState,
    onSignOutClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            user.username?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Text(
                text = user.email!!,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        IconButton(
            onClick = { onSignOutClick() }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = "Exit From Account Icon",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun SignUpCard(
    onSignUpClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.signUpOrIn),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(16.dp))
        IconButton(
            onClick = { onSignUpClick() }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = "Login To Account Icon",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
