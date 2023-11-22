package com.swipe.application

import java.io.Serializable

data class Users(
    val username: String = "",
    val firstname: String = "",
    val lastname: String = "",
    val bio: String = "",
    val profile: Int = 0,
    val password: String = "",
    val profileURL: String? = null
    ) : Serializable