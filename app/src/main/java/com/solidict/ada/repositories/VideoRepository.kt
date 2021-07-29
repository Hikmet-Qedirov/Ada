package com.solidict.ada.repositories

import com.solidict.ada.source.remote.AdaServiceApi
import okhttp3.MultipartBody
import javax.inject.Inject

class VideoRepository
@Inject
constructor(
    private val adaServiceApi: AdaServiceApi,
) {

    suspend fun videoPost(
        authToken: String,
        filePart: MultipartBody.Part,
    ) = adaServiceApi.videoPost(
        auth = "Bearer $authToken",
        file = filePart,
    )

    suspend fun videoPostWithVideoId(
        authToken: String,
        filePart: MultipartBody.Part,
        videoId: Int,
    ) = adaServiceApi.videoVideoIdPost(
        auth = "Bearer $authToken",
        videoId = videoId,
        file = filePart,
    )
}