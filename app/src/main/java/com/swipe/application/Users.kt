package com.swipe.application

import java.io.Serializable

data class Users(
    val userID: String,
    val username: String?,
    val profile: Int,
    val profileURL: String?
    ) : Serializable