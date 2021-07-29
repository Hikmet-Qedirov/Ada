package com.solidict.ada.repositories

import com.solidict.ada.model.auth.AuthRequest
import com.solidict.ada.model.auth.AuthValidateRequest
import com.solidict.ada.model.user.Child
import com.solidict.ada.model.user.UserRequest
import com.solidict.ada.source.remote.AdaServiceApi
import javax.inject.Inject

class AuthRepository
@Inject
constructor(
    private val adaServiceApi: AdaServiceApi,
) {
    suspend fun auth(phoneNumber: String) = adaServiceApi.auth(AuthRequest(phoneNumber))

    suspend fun authValidate(
        userId: Int,
        validationCode: String,
    ) = adaServiceApi.authValidate(userId, AuthValidateRequest(validationCode))

    suspend fun userCheck(authToken: String) = adaServiceApi.userCheck("Bearer $authToken")

    suspend fun userPost(
        authToken: String,
        privacyContract: Boolean,
        reportContract: Boolean,
        childDoctorName: String,
        childEstimatedBirthDate: String,
        childGrams: Int,
        childName: String,
        childRealBirthDate: String,
        childGender: String,
        email: String,
    ) = adaServiceApi.userPost(
        auth = "Bearer $authToken",
        userRequest = UserRequest(
            privacyContract = privacyContract,
            reportContract = reportContract,
            child = Child(
                doctorName = childDoctorName,
                estimatedBirthDate = childEstimatedBirthDate,
                grams = childGrams,
                name = childName,
                realBirthDate = childRealBirthDate,
                sexuality = childGender
            ),
            email = email
        ))


}