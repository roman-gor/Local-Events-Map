package com.gorman.domainmodel

data class MapEvent(
    val localId: Int = 0,
    val remoteId: String? = null,
    val name: String? = null,
    val description: String? = null,
    val city: String? = null,
    val address: String? = null,
    val coordinates: String? = null,
    val link: String? = null,
    val photo: String? = null,
    val category: String? = null,
    val price: Int? = null,
    val isFavourite: Boolean = false,
    val isSelected: Boolean = false
)
