package com.gorman.navigation.navigator

import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.flow.Flow

interface IAppNavigator {
    val navigationEvents: Flow<NavIntent>
    fun navigateTo(key: NavKey)
    fun switchTab(key: NavKey)
    fun setRoot(key: NavKey)
    fun goBack()
    fun popToRoot()
}
