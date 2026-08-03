package com.portfolio.notelensai

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NoteLensApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (
            BuildConfig.FIREBASE_CONFIGURED &&
            FirebaseApp.initializeApp(this) != null
        ) {
            installAppCheckProvider()
        }
    }
}