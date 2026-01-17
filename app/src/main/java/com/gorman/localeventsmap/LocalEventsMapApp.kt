package com.gorman.localeventsmap

import android.app.Application
import android.content.Context
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LocalEventsMapApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initYandexMapKit(this)
    }
}

private fun initYandexMapKit(context: Context) {
    try {
        MapKitFactory.setApiKey(BuildConfig.YANDEX_KEY)
        MapKitFactory.setLocale("en_US")
        MapKitFactory.initialize(context)
    } catch (e: Exception) {
        error("${e.message}")
    }
}
