package com.solidict.ada.viewmodel

import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solidict.ada.model.video.Video
import com.solidict.ada.repositories.VideoRepository
import com.solidict.ada.util.SaveDataPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
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
    val videoPost: LiveData<Response<Video>> get() = _videoPost

    fun videoPost() = viewModelScope.launch {
        _videoPost.value = null
        val token = saveDataPreferences.readToken()!!
        val videoId = saveDataPreferences.readVideoId()
        val fileUri = saveDataPreferences.readVideoUri()!!
        val filePart = makeMultiPartBodyPart(fileUri)
        Log.d(TAG, "videoPost token :: $token")
        Log.d(TAG, "videoPost videoId :: $videoId")
        Log.d(TAG, "videoPost videoId :: $fileUri")
        Log.d(TAG, "videoPost filePart :: $filePart")
        if (videoId.isNullOrEmpty()) {
            val response = videoRepository.videoPost(
                token,
                filePart
            )
            _videoPost.value = response
            Log.d(
                TAG,
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
            """
            )
        } else {
            val response = videoRepository.videoPostWithVideoId(
                token,
                filePart,
                videoId.toInt()
            )
            _videoPost.value = response
            saveDataPreferences.clearVideoId()
            saveDataPreferences.clearVideoUri()
            Log.d(
                TAG,
                """
                fun videoPostWithId response :::
                $response
                fun videoPostWithId headers :::
                ${response.headers()}
                fun videoPostWithId code :::
                ${response.code()}
                fun videoPostWithId message :::
                ${response.message()}
                fun videoPostWithId body :::
                ${response.body()}
            """
            )
        }
    }

    private fun makeMultiPartBodyPart(filePart: String): MultipartBody.Part {
        val path = filePart.toUri().path!!
        Log.d(TAG, "makeMultiPartBodyPart path : $path")
        val file = File(path)
        Log.d(TAG, "makeMultiPartBodyPart file : $file")

        return MultipartBody.Part.createFormData(
            "file",
            file.name,
            file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        )

    }
}