package com.example.instagramsaver.settings

import android.content.Context

/**
 * Created by binary on 3/6/17.
 */
class SettingsImpl(val context: Context): Settings {

    private val SETTINGS = "settings"
    private val IS_CLIPBOARD_MONITOR_SERVICE_RUNNING = "is_clipboard_monitor_service_running"
    val mSP = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE)

    override fun isClipboardMonitorServiceRunning(): Boolean {
        return mSP.getBoolean(IS_CLIPBOARD_MONITOR_SERVICE_RUNNING, false)
    }

    override fun setClipboardMonitorServiceRunning(isRunning: Boolean) {
        mSP.edit().putBoolean(IS_CLIPBOARD_MONITOR_SERVICE_RUNNING, isRunning).apply()
    }
}