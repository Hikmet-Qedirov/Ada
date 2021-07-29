package com.solidict.ada.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.solidict.ada.model.video.Video
import com.solidict.ada.repositories.VideoRepository
import com.solidict.ada.util.SaveDataPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

private const val TAG = "TestVideoViewModel"

@HiltViewModel
class VideoViewModel
@Inject
constructor(
    private val videoRepository: VideoRepository,
    private val saveDataPreferences: SaveDataPreferences,
) : ViewModel() {
    // video post
    private var _videoPost: MutableLiveData<Response<Video>> = MutableLiveData()
    val videoPost: LiveData<Response<Video>> = _videoPost

    private var _videoVideoIdPost: MutableLiveData<Response<Video>> = MutableLiveData()
    val videoVideoIdPost: LiveData<Response<Video>> = _videoVideoIdPost

    suspend fun videoPost(
        filePart: MultipartBody.Part,
    ) {
        _videoPost.value = null
        val token = saveDataPreferences.readToken()!!
        Log.d(TAG, "videoPost token :: $token")
        val response = videoRepository.videoPost(
            authToken = token,
            filePart = filePart)
        Log.d(TAG,
            """
                fun videoPost response :::
                $response
                fun videoPost headers :::
                ${response.headers()}
                fun videoPost code :::
                ${response.code()}
                fun videoPost message :::
                ${response.message()}
                fun videoPost body :::
                ${response.body()}
            """)
        _videoPost.value = response

    }

    suspend fun videoPostWithVideoId(
        videoId: Int,
        filePart: MultipartBody.Part,
    ) {
        _videoVideoIdPost.value = null
        val token = saveDataPreferences.readToken()!!
        Log.d(TAG, "videoPostWithVideoId token :: $token")
        val response = videoRepository.videoPostWithVideoId(
            authToken = token,
            videoId = videoId,
            filePart = filePart,
        )
        _videoVideoIdPost.value = response
        Log.d(TAG,
            """
                fun videoPostWithVideoId response :::
                $response
                fun videoPostWithVideoId headers :::
                ${response.headers()}
                fun videoPostWithVideoId code :::
                ${response.code()}
                fun videoPostWithVideoId message :::
                ${response.message()}
                fun videoPostWithVideoId body :::
                ${response.body()}
            """)
    }

}