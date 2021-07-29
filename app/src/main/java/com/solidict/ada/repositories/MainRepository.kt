package com.solidict.ada.repositories

import com.solidict.ada.source.remote.AdaServiceApi
import javax.inject.Inject

class MainRepository
@Inject
constructor(
    private val adaServiceApi: AdaServiceApi,
) {
    suspend fun getUserData(authToken: String) =
        adaServiceApi.userGet("Bearer $authToken")

    suspend fun videoDemandReportPost(authToken: String) =
        adaServiceApi.videoDemandReportPost("Bearer $authToken")

    suspend fun videoCanCreateGet(authToken: String) =
        adaServiceApi.videoCanCreateGet("Bearer $authToken")

    suspend fun videoListGet(authToken: String) =
        adaServiceApi.videoListGet("Bearer $authToken")


}