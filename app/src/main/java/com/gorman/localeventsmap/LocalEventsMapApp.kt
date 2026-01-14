package com.gorman.localeventsmap

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LocalEventsMapApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
