package com.swipe.application

import android.content.Context
import android.content.SharedPreferences

class UserSession(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    var userName: String?
        get() = prefs.getString("user_name", "heheWOW23")
        set(value) = prefs.edit().putString("user_name", value).apply()

    var userId: String?
        get() = prefs.getString("user_id", "12346")
        set(value) = prefs.edit().putString("user_id", value).apply()

    var likedGameIds: MutableSet<String>?
        get() = prefs.getStringSet("liked_game_ids", mutableSetOf())
        set(value) = prefs.edit().putStringSet("liked_game_ids", value).apply()
    fun addLikedGameId(gameId: String) {

        val currentIds = likedGameIds ?: mutableSetOf()
        currentIds.add(gameId)
        likedGameIds = currentIds // This will trigger the 'set' method and save the changes
    }

    fun removeLikedGameId(gameId: String) {
        val currentIds = likedGameIds ?: mutableSetOf()
        currentIds.remove(gameId)
        likedGameIds = currentIds // This will trigger the 'set' method and save the changes
    }

}