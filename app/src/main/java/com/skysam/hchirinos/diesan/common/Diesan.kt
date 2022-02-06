package com.skysam.hchirinos.diesan.common

import android.app.Application
import android.content.Context

/**
 * Created by Hector Chirinos (Home) on 27/12/2021.
 */
class Diesan: Application() {
    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    object Diesan {
        fun getContext(): Context = appContext
    }
}