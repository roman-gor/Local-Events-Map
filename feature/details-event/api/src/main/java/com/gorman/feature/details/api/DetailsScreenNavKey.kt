package com.gorman.feature.details.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class DetailsScreenNavKey(val id: String): NavKey
