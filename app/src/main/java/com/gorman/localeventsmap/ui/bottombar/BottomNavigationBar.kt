package com.gorman.localeventsmap.ui.bottombar

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
import com.gorman.localeventsmap.navigation.TopLevelNavEntries

@Composable
fun BottomNavigationBar(
    currentKey: NavKey?,
    onNavigateTo: (NavKey) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 1f),
        modifier = modifier
    ) {
        TopLevelNavEntries.entries.forEach { entry ->
            NavigationBarItem(
                selected = currentKey == entry.route,
                icon = {
                    Icon(
                        painter = painterResource(entry.icon),
                        contentDescription = stringResource(entry.title)
                    )
                },
                onClick = {
                    onNavigateTo(entry.route)
                },
                label = { Text(stringResource(entry.title)) }
            )
        }
    }
}
