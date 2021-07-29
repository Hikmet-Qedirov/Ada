package com.solidict.ada.model.video

data class Video(
    val created: String,
    val id: Int,
    val status: String,
    val title: String,
    val updated: String,
    val url: String,
    val weeks: Int
)