package com.solidict.ada.di

import com.solidict.ada.repositories.VideoRepository
import com.solidict.ada.util.TokenPreferences
import com.solidict.ada.viewmodel.VideoViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
class ServiceAppModule {

    @ServiceScoped
    @Provides
    fun provideVideoViewModel(
        videoRepository: VideoRepository,
        tokenPreferences: TokenPreferences,
    ) = VideoViewModel(videoRepository, tokenPreferences)

}