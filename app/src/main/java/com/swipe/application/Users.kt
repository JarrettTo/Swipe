package com.swipe.application

import java.io.Serializable

data class Users(
    val userID: String = "",
    val username: String? = null,
    val profile: Int = 0,
    val profileURL: String? = null
    ) : Serializable