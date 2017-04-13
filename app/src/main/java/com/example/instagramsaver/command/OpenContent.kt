package com.example.instagramsaver.command

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.instagramsaver.VideoViewActivity
import com.example.instagramsaver.entity.Content
import java.io.File

/**
 * Created by binary on 3/9/17.
 */
class OpenContent(val content:Content, val context: Context) : ContentCommand {
    override fun execute() {
        val i:Intent
        if(content.isPhoto) {
            i = Intent()
            i.action = Intent.ACTION_VIEW;
            i.setDataAndType(Uri.fromFile(File(content.path)), "image/*");
        } else {
            i = Intent(context, VideoViewActivity::class.java)
            i.putExtra(VideoViewActivity.ARG_VIDEO_VIEW_CONTENT, content.path)
        }
        context.startActivity(i)
    }

}