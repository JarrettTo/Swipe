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

    var playlist: MutableSet<String>?
        get() = prefs.getStringSet("groups", mutableSetOf())
        set(value) = prefs.edit().putStringSet("groups", value).apply()

    fun addLikedGameId(gameId: String) {

        val currentIds = likedGameIds?.toMutableSet() ?: mutableSetOf()
        currentIds.add(gameId)
        likedGameIds = currentIds // This will trigger the 'set' method and save the changes
    }

    fun removeLikedGameId(gameId: String) {
        val currentIds = likedGameIds?.toMutableSet() ?: mutableSetOf()
        currentIds.remove(gameId)
        likedGameIds = currentIds // This will trigger the 'set' method and save the changes
    }

    fun addGroupId(groupId: String) : Boolean {
        if(groups?.contains(groupId) == true){
            return false
        }
        val currentIds = groups?.toMutableSet() ?: mutableSetOf()
        currentIds.add(groupId)
        groups = currentIds // This will trigger the 'set' method and save the changes
        return true
    }

    fun addPlaylistId(playlistId: String) : Boolean {
        if(playlist?.contains(playlistId) == true){
            return false
        }
        val currentIds = groups?.toMutableSet() ?: mutableSetOf()
        currentIds.add(playlistId)
        playlist = currentIds
        return true
    }

    fun removePlaylistId(playlistId: String) {
        val currentIds = playlist?.toMutableSet() ?: mutableSetOf()
        currentIds.remove(playlistId)
        playlist = currentIds
    }

}