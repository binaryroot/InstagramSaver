package com.example.instagramsaver.br

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.instagramsaver.App
import com.example.instagramsaver.service.ClipboardMonitorService
import com.example.instagramsaver.utility.L


/**
 * Created by binary on 3/6/17.
 */
class BootCompletedIntentReceiver : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        val isNeedRunService = (p0?.applicationContext as App).getSettings()?.isClipboardMonitorServiceRunning()
        if (isNeedRunService!!) {
            val pushIntent = Intent(p0, ClipboardMonitorService::class.java)
            p0?.startService(pushIntent)
        }
    }

}