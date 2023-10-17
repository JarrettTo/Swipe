package com.swipe.application

data class Games(
    val imageId: Int,
    val gameName: String?,
    val description: String?,
    val genre: ArrayList<String>?,
    var videoId: Int
)
