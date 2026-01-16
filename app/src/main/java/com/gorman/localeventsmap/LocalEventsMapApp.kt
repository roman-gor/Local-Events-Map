package com.gorman.localeventsmap

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LocalEventsMapApp : Application() {
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(BuildConfig.YANDEX_KEY)
        MapKitFactory.initialize(this)
        MapKitFactory.getInstance().onStart()
    }
}
