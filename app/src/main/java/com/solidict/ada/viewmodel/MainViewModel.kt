package com.solidict.ada.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.solidict.ada.model.user.IsReportable
import com.solidict.ada.model.user.UserResponse
import com.solidict.ada.model.video.VideoReportResponse
import com.solidict.ada.model.video.VideoResponse
import com.solidict.ada.repositories.MainRepository
import com.solidict.ada.util.SaveDataPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Response
import javax.inject.Inject

private const val TAG = "TestMainViewModel"

@HiltViewModel
class MainViewModel
@Inject
constructor(
    private val mainRepository: MainRepository,
    private val saveDataPreferences: SaveDataPreferences,
) : ViewModel() {

    val userData: LiveData<Response<UserResponse>> = liveData {
        val token = saveDataPreferences.readToken()!!
        Log.d(TAG, "getUserData token :: $token")
        val response = mainRepository.getUserData(token)
        Log.d(TAG,
            """
                fun getUserData response :::
                $response
                fun getUserData headers :::
                ${response.headers()}
                fun getUserData code :::
                ${response.code()}
                fun getUserData message :::
                ${response.message()}
                fun getUserData body :::
                ${response.body()}
            """)
        emit(response)
    }

    val videoDemandReportPost: LiveData<Response<VideoReportResponse>> = liveData {
        val token = saveDataPreferences.readToken()!!
        Log.d(TAG, "videoDemandReportPost token :: $token")
        val response = mainRepository.videoDemandReportPost(token)
        Log.d(TAG,
            """
                fun videoDemandReportPost response :::
                $response
                fun videoDemandReportPost headers :::
                ${response.headers()}
                fun videoDemandReportPost code :::
                ${response.code()}
                fun videoDemandReportPost message :::
                ${response.message()}
                fun videoDemandReportPost body :::
                ${response.body()}
            """)
        emit(response)
    }

    val videoCanCreateGet: LiveData<Response<IsReportable>> = liveData {
        val token = saveDataPreferences.readToken()!!
        Log.d(TAG, "videoCanCreateGet token :: $token")
        val response = mainRepository.videoCanCreateGet(token)
        Log.d(TAG,
            """
                fun videoCanCreateGet response :::
                $response
                fun videoCanCreateGet headers :::
                ${response.headers()}
                fun videoCanCreateGet code :::
                ${response.code()}
                fun videoCanCreateGet message :::
                ${response.message()}
                fun videoCanCreateGet body :::
                ${response.body()}
            """)
        emit(response)
    }

    val videoListGet: LiveData<Response<VideoResponse>> = liveData {
        val token = saveDataPreferences.readToken()!!
        Log.d(TAG, "videoListGet token :: $token")
        val response = mainRepository.videoListGet(token)
        Log.d(TAG,
            """
                fun videoListGet response :::
                $response
                fun videoListGet headers :::
                ${response.headers()}
                fun videoListGet code :::
                ${response.code()}
                fun videoListGet message :::
                ${response.message()}
                fun videoListGet body :::
                ${response.body()}
            """)
        emit(response)
    }

}