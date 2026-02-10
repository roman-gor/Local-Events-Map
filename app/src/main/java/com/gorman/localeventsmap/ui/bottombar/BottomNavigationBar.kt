package com.gorman.localeventsmap.ui.bottombar

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.NavKey
import com.gorman.localeventsmap.navigation.NavEntries

@Composable
fun BottomNavigationBar(
    currentKey: NavKey?,
    onNavigateTo: (NavKey) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 1f),
        windowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier
    ) {
        NavEntries.entries.forEach { destination ->
            NavigationBarItem(
                selected = currentKey == destination.route,
                icon = {
                    Icon(
                        painter = painterResource(destination.icon),
                        contentDescription = stringResource(destination.title)
                    )
                },
                onClick = {
                    onNavigateTo(destination.route)
                },
                label = { Text(stringResource(destination.title)) }
            )
        }
    }
}
