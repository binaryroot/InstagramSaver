package com.example.instagramsaver

import android.content.Context
import android.os.Environment
import com.example.instagramsaver.entity.Content
import java.io.File
import android.net.Uri

import android.widget.Toast
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.content.ActivityNotFoundException



/**
 * Created by binary on 3/7/17.
 */
class FileUtils {
    companion object {
        val ROOT_FOLDER = "instagram_saver"

        @JvmStatic
        fun loadSavedPhotos(): List<Content> {
            val path = Environment.getExternalStorageDirectory().path.toString() + "/" +ROOT_FOLDER
            val file = File(path)
            if(!file.exists()) {
                file.mkdir()
            }
            val result:MutableList<Content> = mutableListOf<Content>()

            file.list().forEach {
                result.add(Content(path+"/"+it))
            }

            return result;
        }

        fun removeFileByPath(path:String): Boolean {
            val f = File(path)
            return f.delete()
        }


        fun openSavedFolderViaExplorer(context: Context){
            val path = File(Environment.getExternalStorageDirectory().path.toString() + "/" +ROOT_FOLDER)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.fromFile(path), "resource/folder")

            if (intent.resolveActivityInfo(context.packageManager, 0) != null) {
                context.startActivity(intent)
            } else {
                Toast.makeText(context,context.getString(R.string.can_not_open),Toast.LENGTH_SHORT).show()
            }
        }

    }
}