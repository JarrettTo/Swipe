package com.swipe.application

import java.io.Serializable

data class Games(
    val gameId: Int,
    val imageId: Int,
    val gameName: String?,
    val description: String?,
    val genre: ArrayList<String>?,
    val platform: ArrayList<String>?,
    val price: String?,
    var videoId: Int,
    val similarGames: ArrayList<Games>?,
    val popularPlayers: ArrayList<Users>?,
    val reviews: ArrayList<Reviews>?
) : Serializable
