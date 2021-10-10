package com.yasincidem.duplex

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DuplexApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
    }
}
