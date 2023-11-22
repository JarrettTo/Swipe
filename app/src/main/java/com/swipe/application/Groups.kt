package com.swipe.application

import java.io.Serializable

data class Groups(
    val id: String,
    val name: String,
    val creator: String,
    var count: Int,
    val desc: String,
    val image: String,
    val likes: ArrayList<Games>,
    val playlists: ArrayList<String>,
    val users: ArrayList<String>
) : Serializable