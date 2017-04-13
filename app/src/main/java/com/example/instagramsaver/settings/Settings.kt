package com.example.instagramsaver.settings

/**
 * Created by binary on 3/6/17.
 */
interface Settings {

    fun isClipboardMonitorServiceRunning(): Boolean

    fun setClipboardMonitorServiceRunning(isRunning: Boolean)
}