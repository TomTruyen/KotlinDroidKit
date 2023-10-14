package com.tomtruyen.kotlindroidkit.core.handlers

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.gson.Gson
import kotlin.system.exitProcess

class GlobalExceptionHandler private constructor(
    private val applicationContext: Context,
    private val defaultHandler: Thread.UncaughtExceptionHandler,
    private val activityToBeLaunched: Class<*>
): Thread.UncaughtExceptionHandler {
    override fun uncaughtException(t: Thread, e: Throwable) {
        try {
            launchActivity(e)
            exitProcess(0)
        } catch (e: Exception) {
            defaultHandler.uncaughtException(t, e)
        }
    }

    private fun launchActivity(exception: Throwable) {
        val intent = Intent(applicationContext, activityToBeLaunched).apply {
            putExtra(INTENT_DATA_NAME, Gson().toJson(exception))
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }

        applicationContext.startActivity(intent)
    }

    companion object {
        private const val INTENT_DATA_NAME = "CrashData"
        private const val TAG = "GlobalExceptionHandler"

        fun initialize(
            applicationContext: Context,
            activityToBeLaunched: Class<*> = CrashActivity::class.java
        ) {
            val handler = GlobalExceptionHandler(
                applicationContext,
                Thread.getDefaultUncaughtExceptionHandler() as Thread.UncaughtExceptionHandler,
                activityToBeLaunched
            )

            Thread.setDefaultUncaughtExceptionHandler(handler)
        }

        fun getThrowableFromIntent(intent: Intent): Throwable? {
            return try {
                Gson().fromJson(intent.getStringExtra(INTENT_DATA_NAME), Throwable::class.java)
            } catch (e: Exception) {
                Log.e(TAG, "getThrowableFromIntent: ", e)
                null
            }
        }
    }
}