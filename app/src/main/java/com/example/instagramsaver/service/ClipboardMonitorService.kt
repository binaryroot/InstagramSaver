
package com.example.instagramsaver.service

import android.app.Notification
import android.app.NotificationManager

import android.app.Service
import android.content.BroadcastReceiver
import android.content.ClipboardManager
import android.content.Context

import android.content.IntentFilter
import android.support.v4.app.TaskStackBuilder
import android.text.TextUtils
import android.util.Log

import com.example.instagramsaver.MainActivity
import com.example.instagramsaver.R
import org.jsoup.Jsoup
import java.util.concurrent.Executors
import java.util.Date
import java.io.*
import java.net.MalformedURLException
import java.net.URL
import com.example.instagramsaver.FileUtils
import android.content.Intent
import android.app.PendingIntent
import android.graphics.Bitmap
import android.os.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget

class ClipboardMonitorService : Service() {

    private var mHistoryFile: File? = null
    private val mThreadPool = Executors.newSingleThreadExecutor()
    private var mClipboardManager: ClipboardManager? = null

    private var notifyServiceReceiver: NotifyServiceReceiver? = null
    override fun onCreate() {
        super.onCreate()
        notifyServiceReceiver = NotifyServiceReceiver()
        mHistoryFile = File(getExternalFilesDir(null), FILENAME)
        mClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        mClipboardManager!!.addPrimaryClipChangedListener(
                mOnPrimaryClipChangedListener)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, buildNotification(null))
        return super.onStartCommand(intent, flags, startId)
    }

    private fun buildNotification (bitmap:Bitmap?): Notification {
        val intentFilter = IntentFilter()
        intentFilter.addAction(ACTION)
        registerReceiver(notifyServiceReceiver, intentFilter)
        val myIntent = Intent(applicationContext, MainActivity::class.java)
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addParentStack(MainActivity::class.java)
        stackBuilder.addNextIntent(myIntent)

        // Send Notification
        val notificationTitle = "Instagram content saver"
        val notificationText = ""
        val pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationIntent  = packageManager.getLaunchIntentForPackage("com.instagram.android");
        val pendingNotificationIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0)


        val notificationBuilder =  Notification.Builder(this)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText).setSmallIcon(R.drawable.ic_stat_name)
                .setContentIntent(pendingIntent).addAction(R.drawable.ic_menu_camera,getString(R.string.instagram),pendingNotificationIntent)
        bitmap?.let {
            notificationBuilder.setStyle(Notification.BigPictureStyle().bigPicture(bitmap))
        }

        val notification = notificationBuilder.build()
        notification.flags = notification.flags or Notification.FLAG_ONGOING_EVENT
        return notification
    }

    override fun onDestroy() {
        super.onDestroy()

        if (mClipboardManager != null) {
            mClipboardManager!!.removePrimaryClipChangedListener(
                    mOnPrimaryClipChangedListener)
        }
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.let {
            it.cancel(0)
        }

        unregisterReceiver(notifyServiceReceiver)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private val isExternalStorageWritable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            if (Environment.MEDIA_MOUNTED == state) {
                return true
            }
            return false
        }

    private val mOnPrimaryClipChangedListener = ClipboardManager.OnPrimaryClipChangedListener {
        Log.d(TAG, "onPrimaryClipChanged")
        val clip = mClipboardManager!!.primaryClip
        mThreadPool.execute(WriteHistoryRunnable(
                clip.getItemAt(0).text, applicationContext))
    }

    private inner class WriteHistoryRunnable(private val mTextToWrite: CharSequence, val context: Context) : Runnable {
        private val mNow: Date

        init {
            mNow = Date(System.currentTimeMillis())
        }

        override fun run() {
            if (TextUtils.isEmpty(mTextToWrite)) {
                return
            }

            if (isExternalStorageWritable) {
                Log.i(TAG, "Writing new clip to history:")
                Log.i(TAG, mTextToWrite.toString())
                if (mTextToWrite.startsWith(HTTPS_INSTAGRAM)) {
                    loadPage(mTextToWrite.toString())
                }
            } else {
                Log.w(TAG, "External storage is not writable!")
            }
        }


        private fun loadPage(urlToLoad: String): String? {
            val url: URL
            var inputStream: InputStream? = null
            val br: BufferedReader
            var line: String? = null

            try {
                url = URL(urlToLoad)
                inputStream = url.openStream()
                br = BufferedReader(InputStreamReader(inputStream))

                var l:String? = null
                var sb :StringBuilder = StringBuilder()

                while (br.readLine().let { l = it ; it != null }) {
                    println(l)
                    sb.append(l)
                }
                if(!sb.isEmpty()) {
                    val contentUrl = parseResponse(sb.toString())
                    val path = loadContent(contentUrl)
                    updateNotification(path)
                }

            } catch (mue: MalformedURLException) {
                mue.printStackTrace()
            } catch (ioe: IOException) {
                ioe.printStackTrace()
            } finally {
                try {
                    if (inputStream != null) inputStream!!.close()
                } catch (ioe: IOException) {

                }

            }

            return line
        }

        private fun updateNotification(pathToImage: String?): Unit {
            pathToImage?.let {
                if(!TextUtils.isEmpty(it)) {
                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    Handler(Looper.getMainLooper()).post {
                        Glide.with(applicationContext)
                                .load(pathToImage)
                                .asBitmap()
                                .into(object :SimpleTarget<Bitmap>(){
                                    override fun onResourceReady(resource: Bitmap?, glideAnimation: GlideAnimation<in Bitmap>?) {
                                        notificationManager.notify(0, buildNotification(resource))
                                    }
                                })
                    }
                    }
                }
            }


        private fun parseResponse(response: String): String {
            val doc = Jsoup.parse(response)

            var d  =  doc.select("meta[property=og:video]")
            if(d == null || d.size == 0) {
                d =  doc.select("meta[property=og:image]")
            }
            if(d != null) {
                d.forEach {
                    return it.attr("content")
                }
            }

           return ""
        }

        fun loadContent(urlContent: String) :String? {
            try {
                val url = URL(urlContent)
                val connection = url.openConnection()
                connection.connect()
                val input = BufferedInputStream(connection.inputStream)
                val fileName = urlContent.substring(urlContent.lastIndexOf('/') + 1)
                if(!File(Environment.getExternalStorageDirectory().path.toString() + "/" + FileUtils.ROOT_FOLDER).exists()) {
                    File(Environment.getExternalStorageDirectory().path.toString() + "/" + FileUtils.ROOT_FOLDER).mkdir()
                }
                val path = Environment.getExternalStorageDirectory().path.toString() + "/" + FileUtils.ROOT_FOLDER+"/"+fileName
                val output = FileOutputStream(path)

                val data = ByteArray(1024)
                var total: Long = 0
                var count: Int = 0
                while (input.read(data).let { count = it ; it !=-1 }) {
                    total += count.toLong()
                    output.write(data, 0, count)
                }

                output.flush()
                output.close()
                input.close()
                return path
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }


    }

    inner class NotifyServiceReceiver : BroadcastReceiver() {
        override fun onReceive(arg0: Context, arg1: Intent) {
            val rqs = arg1.getIntExtra("RQS", 0)
            if (rqs == RQS_STOP_SERVICE) {
                stopSelf()
            }
        }
    }

    companion object {
        private val TAG = "ClipboardManager"
        private val FILENAME = "clipboard-history.txt"
        private val ACTION = "NotifyServiceAction"
        private val RQS_STOP_SERVICE = 1
        private val HTTPS_INSTAGRAM = "https://www.instagram.com/p/";
    }
}
