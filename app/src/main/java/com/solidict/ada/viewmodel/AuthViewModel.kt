package com.solidict.ada.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.solidict.ada.model.auth.AuthResponse
import com.solidict.ada.model.auth.AuthValidateResponse
import com.solidict.ada.model.user.UserCheckResponse
import com.solidict.ada.model.user.UserResponse
import com.solidict.ada.repositories.AuthRepository
import com.solidict.ada.util.TokenPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

private const val TAG = "TestAuthViewModel"

@HiltViewModel
class AuthViewModel
@Inject
constructor(
    private val authRepository: AuthRepository,
    private val tokenPreferences: TokenPreferences,
) : ViewModel() {
    private var _authResponse: MutableLiveData<Response<AuthResponse>> = MutableLiveData()
    val authResponse: LiveData<Response<AuthResponse>> get() = _authResponse

    private var _authValidate: MutableLiveData<Response<AuthValidateResponse>> = MutableLiveData()
    val authValidate: LiveData<Response<AuthValidateResponse>> get() = _authValidate

    val userCheck: LiveData<Response<UserCheckResponse>> = liveData {
        val token = tokenPreferences.readToken()!!
        Log.d(TAG, "tokenPreferences :: $token")
        val response = authRepository.userCheck(token)
        Log.d(TAG,
            """
                fun userCheck response :::
                $response
                fun userCheck headers :::
                ${response.headers()}
                fun userCheck code :::
                ${response.code()}
                fun userCheck message :::
                ${response.message()}
                fun userCheck body :::
                ${response.body()}
            """)
        emit(response)
    }

    private var _userPost: MutableLiveData<Response<UserResponse>> = MutableLiveData()
    val userPost: LiveData<Response<UserResponse>> get() = _userPost

    val userExist: LiveData<Boolean> = liveData {
        val token = tokenPreferences.readToken()
        Log.d(TAG, "$token")
        if (token != null) {
            emit(true)
        } else {
            emit(false)
        }
    }

    fun auth(number: String) = viewModelScope.launch {
        _authResponse.value = null
        val response = authRepository.auth(number)
        _authResponse.value = response
        Log.d(TAG,
            """
                fun auth response :::
                $response
                fun auth headers :::
                ${response.headers()}
                fun auth code :::
                ${response.code()}
                fun auth message :::
                ${response.message()}
                fun auth body :::
                ${response.body()}
            """)
    }

    fun authValidate(userID: Int, validationCode: String) = viewModelScope.launch {
        _authValidate.value = null
        val response = authRepository.authValidate(userID, validationCode)
        if (response.isSuccessful) {
            val token = response.body()!!.token
            tokenPreferences.saveToken(token)
            userCheck
        }
        _authValidate.value = response
        Log.d(TAG,
            """
                fun authValidate response :::
                $response
                fun authValidate headers :::
                ${response.headers()}
                fun authValidate code :::
                ${response.code()}
                fun authValidate message :::
                ${response.message()}
                fun authValidate body :::
                ${response.body()}
            """)
    }

    fun userPost(
        privacyContract: Boolean,
        reportContract: Boolean,
        childDoctorName: String,
        childEstimatedBirthDate: String,
        childGrams: Int,
        childName: String,
        childRealBirthDate: String,
        childGender: String,
        email: String,
    ) = viewModelScope.launch {
        _userPost.value = null
        val token = tokenPreferences.readToken()!!
        val response = authRepository.userPost(
            authToken = token,
            privacyContract = privacyContract,
            reportContract = reportContract,
            childDoctorName = childDoctorName,
            childEstimatedBirthDate = childEstimatedBirthDate,
            childGrams = childGrams,
            childName = childName,
            childRealBirthDate = childRealBirthDate,
            childGender = childGender,
            email = email,
        )
        _userPost.value = response
        Log.d(TAG,
            """
                fun userPost response :::
                $response
                fun userPost headers :::
                ${response.headers()}
                fun userPost code :::
                ${response.code()}
                fun userPost message :::
                ${response.message()}
                fun userPost body :::
                ${response.body()}
            """)
    }


}