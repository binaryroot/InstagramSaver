package com.example.instagramsaver.utility

import android.util.Log

import com.example.instagramsaver.BuildConfig


/**
 * Advanced logger
 */
object L {

    /**
     * Prints error message.
     *
     * @param msg message value
     */
    fun e(msg: Int) {
        if (BuildConfig.DEBUG) {
            val t = Throwable()
            val elements = t.stackTrace

            val callerClassName = elements[1].getClassName()
            val callerMethodName = elements[1].getMethodName()

            Log.e(callerClassName, callerMethodName + " :: " + msg)
        }
    }

    /**
     * Prints error message.
     *
     * @param msg message value
     */
    fun e(msg: String, error: Throwable) {
        if (BuildConfig.DEBUG) {
            val t = Throwable()
            val elements = t.stackTrace

            val callerClassName = elements[1].getClassName()
            val callerMethodName = elements[1].getMethodName()

            Log.e(callerClassName, callerMethodName + " :: " + msg, error)
        }
    }

    /**
     * Prints debug message.
     *
     * @param msg message value
     */
    fun d(msg: String) {
        if (BuildConfig.DEBUG) {
            val t = Throwable()
            val elements = t.stackTrace

            val callerClassName = elements[1].getClassName()
            val callerMethodName = elements[1].getMethodName()

            Log.d(callerClassName, callerMethodName + " :: " + msg)
        }
    }
}