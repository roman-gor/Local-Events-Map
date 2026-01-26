package com.gorman.events.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.gorman.common.constants.CategoryConstants
import com.gorman.common.constants.toDisplayName
import com.gorman.events.R
import com.gorman.ui.theme.LocalEventsMapTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@SuppressLint("ComposeModifierMissing")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesDropdownMenu(
    expanded: Boolean,
    header: String,
    onExpandedChange: () -> Unit,
    onItemClick: (String) -> Unit,
    categoriesOptions: CategoriesOptions
) {
    val visibleText = remember(header) { mutableStateOf(header) }
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        ExposedDropdownMenuBox(
            modifier = Modifier
                .fillMaxWidth()
                .padding(LocalEventsMapTheme.dimens.paddingLarge),
            expanded = expanded,
            onExpandedChange = { onExpandedChange() }
        ) {
            CategoriesTextField(
                selectedItems = categoriesOptions.selectedItems.toImmutableList(),
                visibleText = visibleText.value,
                header = header,
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                    .fillMaxWidth(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }

            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange() },
                shape = LocalEventsMapTheme.shapes.large,
                modifier = Modifier
                    .wrapContentHeight()
                    .exposedDropdownSize(),
                containerColor = MaterialTheme.colorScheme.background
            ) {
                categoriesOptions.items.forEach { category ->
                    val title = category.toDisplayName()
                    CategoryMenuItem(
                        checked = category.value in categoriesOptions.selectedItems,
                        onCheckedChange = { onItemClick(category.value) },
                        title = title,
                        category = category.value,
                        onItemClick = { onItemClick(it) }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoriesTextField(
    selectedItems: ImmutableList<String>,
    visibleText: String,
    header: String,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable () -> Unit
) {
    OutlinedTextField(
        value = if (selectedItems.isEmpty()) {
            visibleText
        } else {
            "${stringResource(R.string.selectedCategoriesAmount)}: ${selectedItems.size}"
        },
        onValueChange = {},
        placeholder = {
            Text(
                text = header,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        },
        readOnly = true,
        modifier = modifier,
        trailingIcon = { trailingIcon() },
        shape = LocalEventsMapTheme.shapes.medium
    )
}

@SuppressLint("ComposeModifierMissing")
@Composable
fun CategoryMenuItem(
    checked: Boolean,
    onCheckedChange: () -> Unit,
    title: String,
    category: String,
    onItemClick: (String) -> Unit
) {
    DropdownMenuItem(
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = { onCheckedChange() }
                )
                Text(
                    text = title,
                    modifier = Modifier.padding(horizontal = LocalEventsMapTheme.dimens.paddingMedium)
                )
            }
        },
        onClick = {
            onItemClick(category)
        },
        modifier = Modifier.fillMaxSize()
    )
}

data class CategoriesOptions(
    val items: List<CategoryConstants>,
    val selectedItems: List<String>
)
