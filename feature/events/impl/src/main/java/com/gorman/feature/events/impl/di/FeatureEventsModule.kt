package com.gorman.feature.events.impl.di

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.gorman.feature.events.impl.navigation.featureEventsEntryBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
object FeatureEventsModule {
    @IntoSet
    @Provides
    fun provideFeatureEventsEntryBuilder() : EntryProviderScope<NavKey>.() -> Unit = {
        featureEventsEntryBuilder()
    }
}
