package com.gorman.feature.details.impl.di

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.gorman.feature.details.impl.navigation.featureDetailsEntryBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
object FeatureDetailsModule {
    @IntoSet
    @Provides
    fun provideFeatureDetailsEntryBuilder(): EntryProviderScope<NavKey>.() -> Unit = {
        featureDetailsEntryBuilder()
    }
}
