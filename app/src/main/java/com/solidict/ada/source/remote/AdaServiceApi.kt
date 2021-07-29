package com.solidict.ada.source.remote

import com.solidict.ada.model.auth.AuthRequest
import com.solidict.ada.model.auth.AuthResponse
import com.solidict.ada.model.auth.AuthValidateRequest
import com.solidict.ada.model.auth.AuthValidateResponse
import com.solidict.ada.model.user.IsReportable
import com.solidict.ada.model.user.UserCheckResponse
import com.solidict.ada.model.user.UserRequest
import com.solidict.ada.model.user.UserResponse
import com.solidict.ada.model.video.Video
import com.solidict.ada.model.video.VideoReportResponse
import com.solidict.ada.model.video.VideoResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface AdaServiceApi {
    companion object {
        //  video-controller
        private const val VIDEO_POST = "video"
        private const val VIDEO_VIDEO_ID_POST = "video/{videoId}"
        private const val VIDEO_DEMAND_REPORT_POST = "video/demand/report"
        private const val VIDEO_LIST_GET = "video/list"
        private const val VIDEO_CAN_CREATE_GET = "video/can-create"

        //  user-controller
        private const val USER_GET = "user"
        private const val USER_POST = "user"
        private const val USER_CHECK_GET = "user/check"

        //  auth-controller
        private const val AUTH_POST = "auth"
        private const val AUTH_USER_ID_VALIDATE_POST = "auth/{userId}/validate"
    }

    //  video-controller
    @Multipart
    @POST(VIDEO_POST)
    suspend fun videoPost(
        @Header("Authorization") auth: String,
        @Part file: MultipartBody.Part,
        ): Response<Video>

    @Multipart
    @POST(VIDEO_VIDEO_ID_POST)
    suspend fun videoVideoIdPost(
        @Header("Authorization") auth: String,
        @Path("videoId") videoId: Int,
        @Part file: MultipartBody.Part,
    ): Response<Video>
    // end video-controller

    @POST(VIDEO_DEMAND_REPORT_POST)
    suspend fun videoDemandReportPost(
        @Header("Authorization") auth: String,
    ): Response<VideoReportResponse>

    @GET(VIDEO_LIST_GET)
    suspend fun videoListGet(
        @Header("Authorization") auth: String,
    ): Response<VideoResponse>

    @GET(VIDEO_CAN_CREATE_GET)
    suspend fun videoCanCreateGet(
        @Header("Authorization") auth: String,
    ): Response<IsReportable>

    //  user-controller
    @GET(USER_GET)
    suspend fun userGet(
        @Header("Authorization") auth: String,
    ): Response<UserResponse>

    @POST(USER_POST)
    suspend fun userPost(
        @Header("Authorization") auth: String,
        @Body userRequest: UserRequest,
    ): Response<UserResponse>

    @GET(USER_CHECK_GET)
    suspend fun userCheck(@Header("Authorization") auth: String): Response<UserCheckResponse>

    //  auth-controller
    @POST(AUTH_POST)
    suspend fun auth(@Body phoneNumber: AuthRequest): Response<AuthResponse>

    @POST(AUTH_USER_ID_VALIDATE_POST)
    suspend fun authValidate(
        @Path("userId") userId: Int,
        @Body validationCode: AuthValidateRequest,
    ): Response<AuthValidateResponse>

}