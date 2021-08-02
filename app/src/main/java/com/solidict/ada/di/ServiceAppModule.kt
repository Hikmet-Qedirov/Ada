package com.solidict.ada.di

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.solidict.ada.ui.activities.MainActivity
import com.solidict.ada.R
import com.solidict.ada.repositories.VideoRepository
import com.solidict.ada.util.Constants.Companion.NOTIFICATION_CHANNEL_ID
import com.solidict.ada.util.SaveDataPreferences
import com.solidict.ada.viewmodel.VideoViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceAppModule {

    @ServiceScoped
    @Provides
    fun provideVideoViewModel(
        videoRepository: VideoRepository,
        saveDataPreferences: SaveDataPreferences,
    ) = VideoViewModel(videoRepository, saveDataPreferences)

    @SuppressLint("UnspecifiedImmutableFlag")
    @ServiceScoped
    @Provides
    fun providePendingIntent(@ApplicationContext app: Context): PendingIntent =
        PendingIntent.getActivity(
            app,
            0,
            Intent(app, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

    @ServiceScoped
    @Provides
    fun provideBaseNotificationBuilder(
        @ApplicationContext app: Context,
        pendingIntent: PendingIntent
    ): NotificationCompat.Builder = NotificationCompat.Builder(
        app,
        NOTIFICATION_CHANNEL_ID
    )
        .setAutoCancel(false)
        .setOngoing(true)
        .setSmallIcon(R.drawable.ic_load_video)
        .setContentIntent(pendingIntent)
        .setContentText(app.getString(R.string.notification_video_post_loading))
}