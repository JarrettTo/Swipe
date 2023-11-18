package com.swipe.application

import java.io.Serializable

class Playlist (
    val playlistId: Int,
    val playlistName: String,
    val username: String,
    val imageId: Int?,
    val imageURL: String?,
    val games: ArrayList<Games>?,
) : Serializable