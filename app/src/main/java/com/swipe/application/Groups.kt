package com.swipe.application

import java.io.Serializable

data class Groups(
    val name: String,
    val imageId: Int,
    val description: String,
    val userCount: Int
) : Serializable