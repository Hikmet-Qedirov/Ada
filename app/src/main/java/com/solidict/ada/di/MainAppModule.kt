package com.solidict.ada.di

import android.content.Context
import com.solidict.ada.repositories.AuthRepository
import com.solidict.ada.repositories.MainRepository
import com.solidict.ada.repositories.VideoRepository
import com.solidict.ada.source.remote.AdaServiceApi
import com.solidict.ada.util.ConnectionLiveData
import com.solidict.ada.util.SaveDataPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainAppModule {

    @Singleton
    @Provides
    fun provideTokenPreferences(@ApplicationContext context: Context) = SaveDataPreferences(context)

    @Singleton
    @Provides
    @Named("BASE_URL")
    fun provideBaseUrl(): String {
        return "http://ec2-52-17-33-184.eu-west-1.compute.amazonaws.com:8080"
    }

    @Singleton
    @Provides
    fun provideHeaderInterceptor() = Interceptor { chain ->
        val request = chain.request()
            .newBuilder()
            .addHeader("accept", "*/*")
            .addHeader("Content-Type", "application/json")
            .build()
        chain.proceed(request)
    }


    @Singleton
    @Provides
    fun provideClient(
        interceptor: Interceptor,
    ): OkHttpClient {
        return OkHttpClient
            .Builder()
            .addInterceptor(interceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(
        @Named("BASE_URL") baseUrl: String,
        client: OkHttpClient,
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    @Singleton
    @Provides
    fun provideAdaServiceApi(retrofit: Retrofit): AdaServiceApi =
        retrofit.create(AdaServiceApi::class.java)

    @Singleton
    @Provides
    fun provideConnectionLiveData(@ApplicationContext context: Context) =
        ConnectionLiveData(context)

    @Singleton
    @Provides
    fun provideAuthRepository(adaServiceApi: AdaServiceApi) =
        AuthRepository(adaServiceApi)

    @Singleton
    @Provides
    fun provideMainRepository(
        adaServiceApi: AdaServiceApi,
    ) = MainRepository(adaServiceApi)

    @Singleton
    @Provides
    fun provideVideoRepository(
        adaServiceApi: AdaServiceApi,
    ) = VideoRepository(adaServiceApi)

}