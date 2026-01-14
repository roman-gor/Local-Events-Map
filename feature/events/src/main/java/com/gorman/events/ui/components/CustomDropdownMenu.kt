package com.gorman.events.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gorman.events.R
import com.gorman.events.ui.constants.CategoryConstants
import com.gorman.ui.theme.LocalEventsMapTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdownMenu(
    expanded: Boolean,
    header: String,
    onExpandedChange: () -> Unit,
    onItemClick: (String) -> Unit,
    items: List<CategoryConstants>,
    selectedItems: List<String>
) {
    val visibleText = remember { mutableStateOf(header) }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        ExposedDropdownMenuBox(
            modifier = Modifier
                .fillMaxWidth()
                .padding(LocalEventsMapTheme.dimens.paddingLarge),
            expanded = expanded,
            onExpandedChange = { onExpandedChange() }
        ){
            OutlinedTextField(
                value = if (selectedItems.isEmpty()) visibleText.value
                else "Выбрано: ${selectedItems.size}",
                onValueChange = {},
                placeholder = {
                    Text(
                        text = header,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.surface)
                },
                readOnly = true,
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                    .fillMaxWidth(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSecondary,
                    focusedBorderColor = MaterialTheme.colorScheme.onSecondary,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSecondary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSecondary
                ),
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange() },
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .wrapContentHeight()
                    .exposedDropdownSize(),
                containerColor = MaterialTheme.colorScheme.background
            ) {
                items.forEach { item ->
                    val title = when(item) {
                        CategoryConstants.EDUCATION -> stringResource(R.string.education)
                        CategoryConstants.MUSIC -> stringResource(R.string.music)
                        CategoryConstants.ART -> stringResource(R.string.art)
                        CategoryConstants.SPORT -> stringResource(R.string.sport)
                        CategoryConstants.FOOD -> stringResource(R.string.food)
                        CategoryConstants.CINEMA -> stringResource(R.string.cinema)
                    }
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = item.value in selectedItems,
                                    onCheckedChange = { onItemClick(item.value) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = MaterialTheme.colorScheme.primary,
                                        uncheckedColor = MaterialTheme.colorScheme.onSecondary,
                                        checkmarkColor = Color.White
                                    )
                                )
                                Text(
                                    text = title,
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    modifier = Modifier.padding(horizontal = LocalEventsMapTheme.dimens.paddingMedium)
                                )
                            }
                        },
                        onClick = {
                            onItemClick(item.value)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
