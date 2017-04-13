package com.example.instagramsaver.command

import android.content.Context
import com.example.instagramsaver.entity.Content
import android.support.v7.app.AlertDialog
import com.example.instagramsaver.FileUtils
import com.example.instagramsaver.R
import java.io.File


/**
 * Created by binary on 3/9/17.
 */
class RemoveContent(val content: Content, val context: Context, val listener: (Boolean) -> Unit) : ContentCommand {

    override fun execute() {
        val myQuittingDialogBox = AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.delete))
                .setMessage(context.getString(R.string.do_you_want_to_delete))
                .setPositiveButton(context.getString(R.string.delete), { dialog, whichButton ->
                    listener.invoke(true)
                    FileUtils.removeFileByPath(content.path)
                    dialog.dismiss()
                })
                .setNegativeButton(context.getString(R.string.cancel), { dialog, which -> dialog.dismiss() })
                .create()
        myQuittingDialogBox.show()
    }

}