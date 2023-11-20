package com.swipe.application

import java.io.Serializable

data class Groups(
    val id: String,
    val name: String,
    var count: Int,
    val desc: String,
    val image: String,
    val likes: ArrayList<String>
) : Serializable