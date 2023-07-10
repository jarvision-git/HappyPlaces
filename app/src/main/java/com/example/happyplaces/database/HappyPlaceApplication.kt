package com.example.happyplaces.database

import android.app.Application
import com.google.android.libraries.places.api.Places


class HappyPlaceApplication: Application() {
    val db by lazy {
        HappyPlaceDatabase.getInstance(this)
    }

    override fun onCreate() {
        super.onCreate()

        // Initialize the SDK with the Google Maps Platform API key
        Places.initialize(this, "AIzaSyCFC84h-Fdn-nz1g3uCHYVhvrVEx3P-Kic")
    }
}