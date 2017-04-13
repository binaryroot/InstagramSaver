package com.example.instagramsaver

import android.support.v7.app.AppCompatActivity
import android.widget.VideoView
import android.app.ProgressDialog
import android.os.Bundle
import android.net.Uri
import android.os.Build
import android.widget.MediaController
import com.example.instagramsaver.utility.L
import android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
import android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
import android.view.WindowManager
import android.support.v4.content.ContextCompat






class VideoViewActivity : AppCompatActivity() {

    companion object {
        @JvmStatic
        val ARG_VIDEO_VIEW_CONTENT = "ARG_VIDEO_VIEW_CONTENT"
    }

    private var myVideoView: VideoView? = null
    private var position = 0
    private var progressDialog: ProgressDialog? = null
    private var mediaControls: MediaController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // set the main layout of the activity
        setContentView(R.layout.activity_video_view)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.black))
        }

       val bundle: Bundle? = intent.extras
        var url:String
        if (bundle != null) {
            url = bundle.getString(ARG_VIDEO_VIEW_CONTENT)
        } else {
            throw IllegalArgumentException()
        }

        //set the media controller buttons
        if (mediaControls == null) {
            mediaControls = MediaController(this)
        }

        //initialize the VideoView
        myVideoView = findViewById(R.id.video_view) as VideoView

        // create a progress bar while the video file is loading
        progressDialog = ProgressDialog(this)
        // set a title for the progress bar
        progressDialog!!.setTitle("JavaCodeGeeks Android Video View Example")
        // set a message for the progress bar
        progressDialog!!.setMessage("Loading...")
        //set the progress bar not cancelable on users' touch
        progressDialog!!.setCancelable(false)
        // show the progress bar
        progressDialog!!.show()

        try {
            //set the media controller in the VideoView
            myVideoView!!.setMediaController(mediaControls)
            //set the uri of the video to be played
            myVideoView!!.setVideoURI(Uri.parse(url))

        } catch (e: Exception) {
            L.d(e.toString())
        }

        myVideoView!!.requestFocus()
        //we also set an setOnPreparedListener in order to know when the video file is ready for playback
        myVideoView!!.setOnPreparedListener {
            // close the progress bar and play the video
            progressDialog!!.dismiss()
            //if we have a position on savedInstanceState, the video playback should start from here
            myVideoView!!.seekTo(position)
            if (position == 0) {
                myVideoView!!.start()
            } else {
                //if we come from a resumed activity, video playback will be paused
                myVideoView!!.pause()
            }
        }

    }

    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putInt("Position", myVideoView!!.currentPosition)
        myVideoView!!.pause()
    }

    public override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        position = savedInstanceState.getInt("Position")
        myVideoView!!.seekTo(position)
    }
}
