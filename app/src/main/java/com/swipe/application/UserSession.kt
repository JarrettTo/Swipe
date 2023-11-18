package com.swipe.application

import android.content.Context
import android.content.SharedPreferences

class UserSession(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val sample : MutableSet<String> = mutableSetOf("1")

    var userName: String?
        get() = prefs.getString("user_name", "heheWOW23")
        set(value) = prefs.edit().putString("user_name", value).apply()

    var userId: String?
        get() = prefs.getString("user_id", "12346")
        set(value) = prefs.edit().putString("user_id", value).apply()

    var likedGameIds: MutableSet<String>?
        get() = prefs.getStringSet("liked_game_ids", mutableSetOf())
        set(value) = prefs.edit().putStringSet("liked_game_ids", value).apply()
    var groups: MutableSet<String>?

        get() = prefs.getStringSet("groups", sample)
        set(value) = prefs.edit().putStringSet("groups", value).apply()
    fun addLikedGameId(gameId: String) {

        val currentIds = likedGameIds?.toMutableSet() ?: mutableSetOf()
        currentIds.add(gameId)
        likedGameIds = currentIds // This will trigger the 'set' method and save the changes
    }
    fun addGroupId(groupId: String) {

        val currentIds = groups?.toMutableSet() ?: mutableSetOf()
        currentIds.add(groupId)
        groups = currentIds // This will trigger the 'set' method and save the changes
    }
    fun removeLikedGameId(gameId: String) {
        val currentIds = likedGameIds?.toMutableSet() ?: mutableSetOf()
        currentIds.remove(gameId)
        likedGameIds = currentIds // This will trigger the 'set' method and save the changes
    }

}