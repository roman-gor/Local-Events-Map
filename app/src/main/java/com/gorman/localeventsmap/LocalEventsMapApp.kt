package com.gorman.localeventsmap

import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.android.gms.common.api.ApiException
import com.gorman.work.initializers.Sync
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class LocalEventsMapApp : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        initYandexMapKit(this)
        Sync.initialize(this)
    }
}

private fun initYandexMapKit(context: Context) {
    try {
        MapKitFactory.setApiKey(BuildConfig.YANDEX_KEY)
        MapKitFactory.setLocale("en_US")
        MapKitFactory.initialize(context)
        MapKitFactory.getInstance().onStart()
    } catch (e: ApiException) {
        error("${e.message}")
    }
}
