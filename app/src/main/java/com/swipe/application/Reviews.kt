package com.swipe.application

import java.io.Serializable

class Reviews (
    var reviewID: String = "",
    val user: Users = Users(),
    val gameId: Int = 0,
    val rating: Int = 0,
    val description: String? = null
) : Serializable {

}
