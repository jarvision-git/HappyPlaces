package com.example.happyplaces.database

import android.app.Application


class HappyPlaceApplication: Application() {
    val db by lazy {
        HappyPlaceDatabase.getInstance(this)
    }
}