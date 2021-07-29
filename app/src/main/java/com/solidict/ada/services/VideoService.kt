package com.solidict.ada.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.solidict.ada.MainActivity
import com.solidict.ada.R
import com.solidict.ada.viewmodel.VideoViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MultipartBody
import javax.inject.Inject

private const val TAG = "TestVideoService"

@AndroidEntryPoint
class VideoService : Service() {
    private var isFirstRun = true

    @Inject
    lateinit var viewModel: VideoViewModel

    private lateinit var notificationBuilder: NotificationCompat.Builder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            when (it.action) {
                START_SERVICE -> {
                    if (isFirstRun) {
                        startForegroundService()
                        isFirstRun = false
                        Log.d(TAG, "service mode :: START_SERVICE")
                    } else {
                        Log.d(TAG, "service mode :: RESUME_SERVICE")
                    }
                }
                STOP_SERVICE -> {
                    Log.d(TAG, "service mode :: STOP_SERVICE")

                }
                else -> {
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannelManager(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID, CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    private fun startForegroundService() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannelManager(notificationManager)
        }
        notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_load_video)
            .setContentTitle(getString(R.string.app_name))
            .setContentIntent(getMainActivityPendingIntent())
            .setContentText(getString(R.string.notification_video_post_loading))
            .setProgress(PROGRESS_MAX, PROGRESS_START, true)
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun changeNotificationProgress(progressCurrent: Int) {
        if (progressCurrent <= PROGRESS_MAX) {
            NotificationManagerCompat.from(this).apply {
                notificationBuilder.setProgress(PROGRESS_MAX, progressCurrent, false)
                notify(NOTIFICATION_ID, notificationBuilder.build())
            }
        }
    }

    private fun completedTaskNotification() {
        NotificationManagerCompat.from(this).apply {
            notificationBuilder
                .setAutoCancel(true)
                .setContentText(getString(R.string.notification_video_post_success))
                .setProgress(0, 0, false)
            notify(NOTIFICATION_ID, notificationBuilder.build())
        }
    }

    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this, 0,
        Intent(this, MainActivity::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "VIDEO_SERVICE_CHANNEL_ID"
        private const val CHANNEL_NAME = "ADA"
        private const val NOTIFICATION_ID = 777
        private const val PROGRESS_START = 0
        private const val PROGRESS_MAX = 100
        const val START_SERVICE = "START_SERVICE"
        const val STOP_SERVICE = "STOP_SERVICE"

        fun startCommand(context: Context) =
            Intent(context, VideoService::class.java).also {
                it.action = START_SERVICE
                context.startService(it)
            }

        fun stopCommand(context: Context) {
            Intent(context, VideoService::class.java).also {
                it.action = STOP_SERVICE
                context.stopService(it)
            }
        }
    }

    suspend fun videoPost(filePart: MultipartBody.Part) {
        viewModel.videoPost(filePart)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}
