package com.gorman.database.utils

import androidx.room.TypeConverter

class FiltersCategoriesConverter {
    @TypeConverter
    fun fromCategoriesList(categories: List<String>?): String? {
        return categories?.let { categories.joinToString(separator = ",") }
    }

    @TypeConverter
    fun toCategoriesList(categories: String?): List<String>? {
        return categories?.let { categories.split(",").filter { it.isNotBlank() } }
    }
}
