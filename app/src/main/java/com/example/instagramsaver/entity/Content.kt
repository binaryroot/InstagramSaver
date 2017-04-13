package com.example.instagramsaver.entity


/**
 * Created by binary on 3/7/17.
 */

class Content(val path: String) {
    val isPhoto:Boolean get() {
        val ext = path.substring(path.indexOf(".") + 1)
        return ext.equals("jpg", ignoreCase = true)
                || ext.equals("png", ignoreCase = true)
                || ext.equals("jpeg", ignoreCase = true)
    }
}