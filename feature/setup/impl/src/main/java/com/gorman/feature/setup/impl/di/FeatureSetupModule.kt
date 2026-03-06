package com.gorman.feature.setup.impl.di

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.gorman.feature.setup.impl.navigation.featureSetupEntryBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
object FeatureSetupModule {
    @IntoSet
    @Provides
    fun provideFeatureSetupEntryBuilder(): EntryProviderScope<NavKey>.() -> Unit = {
        featureSetupEntryBuilder()
    }
}
