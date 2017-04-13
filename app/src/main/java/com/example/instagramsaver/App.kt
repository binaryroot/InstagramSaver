package com.example.instagramsaver

import android.app.Application
import com.example.instagramsaver.settings.Settings
import com.example.instagramsaver.settings.SettingsImpl

/**
 * Created by binary on 3/6/17.
 */
class App : Application() {

    private var mSettings: Settings? = null

    override fun onCreate() {
        super.onCreate()
        mSettings = SettingsImpl(applicationContext)
    }

    fun getSettings() : Settings? {
        return mSettings;
    }
}