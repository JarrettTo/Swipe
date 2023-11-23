package com.swipe.application

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class UserSession(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val sample : MutableSet<String> = mutableSetOf("1")
    private val gson = Gson()

    var user: Users?
        get() {
            val userJson = prefs.getString("user", null)
            return if (userJson.isNullOrEmpty()) null else gson.fromJson(userJson, Users::class.java)
        }
        set(value) {
            val userJson = gson.toJson(value)
            prefs.edit().putString("user", userJson).apply()
        }

    var userName = prefs.getString("user_name", user?.username)

    var userId: String?
        get() = prefs.getString("user_id", "12346")
        set(value) = prefs.edit().putString("user_id", value).apply()

    var profileURL: String?
        get() = prefs.getString("user_id", "")
        set(value) = prefs.edit().putString("user_id", value).apply()

    var likedGameIds: MutableSet<String>?
        get() = prefs.getStringSet("liked_game_ids", mutableSetOf())
        set(value) = prefs.edit().putStringSet("liked_game_ids", value).apply()
    var groups: MutableSet<String>?
        get() = prefs.getStringSet("groups", sample)
        set(value) = prefs.edit().putStringSet("groups", value).apply()

    var playlist: MutableSet<String>?
        get() = prefs.getStringSet("playlists", mutableSetOf())
        set(value) = prefs.edit().putStringSet("playlists", value).apply()

    fun addLikedGameId(gameId: String) {
        val currentIds = likedGameIds?.toMutableSet() ?: mutableSetOf()
        currentIds.add(gameId)
        likedGameIds = currentIds
    }

    fun removeLikedGameId(gameId: String) {
        val currentIds = likedGameIds?.toMutableSet() ?: mutableSetOf()
        currentIds.remove(gameId)
        likedGameIds = currentIds
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

    fun removeGroupId(groupId: String): Boolean {
        val currentIds = groups?.toMutableSet()
        if (currentIds != null && currentIds.contains(groupId)) {
            currentIds.remove(groupId)
            groups = currentIds
            return true
        }
        return false
    }

    fun addPlaylistId(playlistId: String) : Boolean {
        if(playlist?.contains(playlistId) == true){
            return false
        }
        val currentIds = playlist?.toMutableSet() ?: mutableSetOf()
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