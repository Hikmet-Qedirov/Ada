package com.solidict.ada.services

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.solidict.ada.R
import com.solidict.ada.util.Constants.Companion.NOTIFICATION_CHANNEL_ID
import com.solidict.ada.util.Constants.Companion.NOTIFICATION_CHANNEL_NAME
import com.solidict.ada.util.Constants.Companion.START_UPLOAD_SERVICE
import com.solidict.ada.util.Constants.Companion.STOP_UPLOAD_SERVICE
import com.solidict.ada.util.Resource
import com.solidict.ada.util.SaveDataPreferences
import com.solidict.ada.viewmodel.VideoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "TestUploadService"

@AndroidEntryPoint
class UploadService : LifecycleService() {
    private var isFirstRun = true
    private var isServiceKilled = false

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    @Inject
    lateinit var viewModel: VideoViewModel

    @Inject
    lateinit var saveDataPreferences: SaveDataPreferences

    private lateinit var currentNotificationBuilder: NotificationCompat.Builder

    override fun onCreate() {
        super.onCreate()
        currentNotificationBuilder = baseNotificationBuilder
        if (!isServiceKilled) {
            viewModel.videoPost.observe(this) { videoResponse ->
                if (videoResponse != null) {
                    when (videoResponse) {
                        is Resource.Success -> {
                            val job = Job()
                            CoroutineScope(Dispatchers.Default + job).launch {
                                saveDataPreferences.clearVideoId()
                                saveDataPreferences.clearVideoUri()
                                job.cancel()
                            }
                            updateNotificationState(true)
                        }
                        is Resource.Error -> {
                            updateNotificationState(false)
                        }
                        is Resource.Loading -> {
                            //gelecekte yeni fonksiyonlar icin
                        }
                    }
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
                    serviceKilled()
                }
                else -> {
                    Log.d(TAG, "UploadService onStartCommand :: else ")
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    private fun startForegroundService() {
        Build.VERSION_CODES.O
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager)
        baseNotificationBuilder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, true)
        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun updateNotificationState(isDone: Boolean) {
        val actionText = getString(R.string.ok)
        val contentText =
            if (isDone) getString(R.string.notification_video_post_success) else getString(R.string.notification_video_post_error)
        val doneIntent = Intent(this, UploadService::class.java).apply {
            action = STOP_UPLOAD_SERVICE
        }
        val pendingIntent =
            PendingIntent
                .getService(
                    this, 1,
                    doneIntent, PendingIntent.FLAG_CANCEL_CURRENT
                )

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // for clear old actions
        currentNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(currentNotificationBuilder, ArrayList<NotificationCompat.Action>())
        }
        if (!isServiceKilled) {
            currentNotificationBuilder = baseNotificationBuilder.apply {
                addAction(
                    R.drawable.ic_success,
                    actionText, pendingIntent
                )
                setProgress(0, 0, false)
                setContentText(contentText)
            }
            notificationManager.notify(
                NOTIFICATION_ID,
                currentNotificationBuilder.build()
            )
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 123
        private const val PROGRESS_CURRENT = 0
        private const val PROGRESS_MAX = 100
    }

    private fun serviceKilled() {
        isFirstRun = true
        isServiceKilled = true
        stopForeground(false)
        stopSelf()
    }
}