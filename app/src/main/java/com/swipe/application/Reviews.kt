package com.swipe.application

import java.io.Serializable

class Reviews (
    val user: Users,
    val gameId: Int,
    val rating: Int,
    val description: String
) : Serializable