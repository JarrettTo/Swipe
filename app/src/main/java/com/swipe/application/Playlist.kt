package com.swipe.application

import java.io.Serializable

class Playlist (
    val playlistId: String,
    val playlistName: String,
    val username: String,
    val imageId: Int?,
    val games: ArrayList<Games>?,
) : Serializable