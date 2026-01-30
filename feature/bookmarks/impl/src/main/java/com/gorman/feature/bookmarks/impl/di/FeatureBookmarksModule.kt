package com.gorman.feature.bookmarks.impl.di

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.gorman.feature.bookmarks.impl.navigation.featureBookmarksEntryBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
object FeatureBookmarksModule {
    @IntoSet
    @Provides
    fun provideFeatureBookmarksEntryBuilder(): EntryProviderScope<NavKey>.() -> Unit = {
        featureBookmarksEntryBuilder()
    }
}
