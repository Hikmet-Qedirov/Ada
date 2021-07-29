package com.solidict.ada.model.auth

data class AuthValidateResponse(
    val token: String,
    val validated: Boolean,
    val expiration: String,
)