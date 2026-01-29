package com.gorman.feature.auth.impl.di

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.gorman.feature.auth.impl.navigation.featureAuthEntryBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
object FeatureAuthModule {
    @IntoSet
    @Provides
    fun provideFeatureAuthEntryBuilder(): EntryProviderScope<NavKey>.() -> Unit = {
        featureAuthEntryBuilder()
    }
}
