package com.swipe.application

import java.io.Serializable

data class Games(
    val gameId: Int = 0,
    val imageId: Int = 0,
    val imageURL: String? = null,
    val gameName: String? = null,
    val description: String? = null,
    val genre: ArrayList<String>? = arrayListOf(),
    val platform: ArrayList<String>? = arrayListOf(),
    val price: String? = null,
    var videoId: Int = 0,
    var videoUrl: String? = null,
    val similarGames: ArrayList<Games>? = arrayListOf(),
    val popularPlayers: ArrayList<Users>? = arrayListOf(),
    val reviews: ArrayList<Reviews>? = arrayListOf()
) : Serializable
