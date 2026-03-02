package com.gorman.deeplinks.di

import com.gorman.deeplinks.DeeplinksStrategy
import com.gorman.deeplinks.strategies.DetailsEventDeeplinksStrategy
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
internal interface DeeplinksModule {
    @Binds
    @IntoSet
    fun bindDetailsEventStrategy(strategy: DetailsEventDeeplinksStrategy): DeeplinksStrategy
}
