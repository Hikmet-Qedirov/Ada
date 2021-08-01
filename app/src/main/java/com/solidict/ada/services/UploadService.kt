package com.solidict.ada.services

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.solidict.ada.R
import com.solidict.ada.util.Constants.Companion.NOTIFICATION_CHANNEL_ID
import com.solidict.ada.util.Constants.Companion.NOTIFICATION_CHANNEL_NAME
import com.solidict.ada.util.Constants.Companion.START_UPLOAD_SERVICE
import com.solidict.ada.util.Constants.Companion.STOP_UPLOAD_SERVICE
import com.solidict.ada.viewmodel.VideoViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "TestUploadService"

@AndroidEntryPoint
class UploadService : LifecycleService() {
    private var isFirstRun = true

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    @Inject
    lateinit var viewModel: VideoViewModel

    private lateinit var currentNotificationBuilder: NotificationCompat.Builder

    override fun onCreate() {
        super.onCreate()
        currentNotificationBuilder = baseNotificationBuilder
        viewModel.videoPost.observe(this) { videoResponse ->
            if (videoResponse != null) {
                if (videoResponse.isSuccessful) {
                    updateNotificationState(true)
                } else {
                    updateNotificationState(false)
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                START_UPLOAD_SERVICE -> {
                    if (isFirstRun) {
                        isFirstRun = false
                        Log.d(TAG, "UploadService onStartCommand :: START_UPLOAD isFirstRun")
                        startForegroundService()
                        viewModel.videoPost()
                    } else {
                        Log.d(TAG, "UploadService onStartCommand :: START_UPLOAD isNotFirstRun")
                    }
                }
                STOP_UPLOAD_SERVICE -> {
                    Log.d(TAG, "UploadService onStartCommand :: STOP_UPLOAD ")
                    stopSelf()
                }
                else -> {
                    Log.d(TAG, "UploadService onStartCommand :: else ")

                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    private fun startForegroundService() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }
        baseNotificationBuilder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, true)
        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun updateNotificationState(isDone: Boolean) {
        val actionText = if (isDone) getString(R.string.ok) else getString(R.string.retry)
        val contentText =
            if (isDone) getString(R.string.notification_video_post_success) else getString(R.string.notification_video_post_error)
        val pendingIntent = if (isDone) {
            val doneIntent = Intent(this, UploadService::class.java).apply {
                action = STOP_UPLOAD_SERVICE
            }
            PendingIntent.getService(this, 1, doneIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        } else {
            val errorIntent = Intent(this, UploadService::class.java).apply {
                action = START_UPLOAD_SERVICE
            }
            PendingIntent.getService(this, 1, errorIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // for clear old actions
        currentNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(currentNotificationBuilder, ArrayList<NotificationCompat.Action>())
        }
        currentNotificationBuilder = baseNotificationBuilder.apply {
            if (isDone) {
                addAction(
                    R.drawable.ic_success,
                    actionText, pendingIntent
                )
                setContentIntent(pendingIntent)
            } else {
                addAction(
                    R.drawable.ic_time_custom,
                    actionText, pendingIntent
                )
            }
            setOngoing(false)
            setProgress(0, 0, false)
            setContentText(contentText)
        }

        notificationManager.notify(NOTIFICATION_ID, currentNotificationBuilder.build())
    }

    companion object {
        private const val NOTIFICATION_ID = 777
        private const val PROGRESS_CURRENT = 0
        private const val PROGRESS_MAX = 100
    }
}