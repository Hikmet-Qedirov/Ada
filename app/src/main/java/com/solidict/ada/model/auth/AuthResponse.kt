package com.solidict.ada.model.auth

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AuthResponse(
    val expiration: String,
    val id: Int,
    val location: String,
): Parcelable