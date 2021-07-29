package com.solidict.ada.model.user

data class UserRequest(
    val privacyContract: Boolean,
    val reportContract: Boolean,
    val child: Child,
    val email: String
)