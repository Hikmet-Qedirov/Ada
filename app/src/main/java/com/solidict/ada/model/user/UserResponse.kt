package com.solidict.ada.model.user

data class UserResponse(
    val child: Child,
    val email: String,
    val id: Int,
    val isReportable: IsReportable,
    val privacyContract: Boolean,
    val reportContract: Boolean,
)