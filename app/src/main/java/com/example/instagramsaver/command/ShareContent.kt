package com.example.instagramsaver.command

import android.content.Context
import com.example.instagramsaver.entity.Content
import android.content.Intent
import android.net.Uri
import com.example.instagramsaver.utility.L
import java.io.File

/**
 * Created by binary on 3/9/17.
 */
class ShareContent(val content: Content, val context: Context) : ContentCommand {

    override fun execute() {
        L.d(content.path)
        val share = Intent(Intent.ACTION_SEND)
        share.type = "image/*, video/*"
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(File(content.path)))
        context.startActivity(Intent.createChooser(share, "Share Image"))
    }

}