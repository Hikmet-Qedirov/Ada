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

//    suspend fun videoPost(
//        filePart: MultipartBody.Part,
//    ) {
//
//        _videoPost.value = response
//
//    }

}