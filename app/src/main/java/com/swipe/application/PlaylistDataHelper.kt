package com.swipe.application

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PlaylistDataHelper {

    suspend fun retrievePlaylists(playlistsId: Set<String>?): ArrayList<Playlist> = withContext(Dispatchers.IO) {
        val playlists = ArrayList<Playlist>()

        playlistsId?.forEach { id ->
            val playlistRef = FirebaseDatabase.getInstance().getReference("test").child("playlists").child(id)
            try {
                val snapshot = playlistRef.get().await()
                if (snapshot.exists()) {
                    val playlistName = snapshot.child("playlistName").getValue(String::class.java) ?: ""
                    val username = snapshot.child("username").getValue(String::class.java) ?: ""
                    val imageId = snapshot.child("imageId").getValue(Int::class.java)
                    val imageURL = snapshot.child("imageURL").getValue(String::class.java) ?: ""
                    val games = ArrayList<Games>()

                    val gamesSnapshot = snapshot.child("games")
                    for (gameSnapshot in gamesSnapshot.children) {
                        val game = gameSnapshot.getValue(Games::class.java)
                        game?.let { games.add(it) }
                    }

                    val playlist = Playlist(id, playlistName, username, imageId, imageURL, games)
                    playlists.add(playlist)
                }
            } catch (e: Exception) {
                Log.e("FirebaseError", "Error fetching playlist data", e)
            }
        }
        return@withContext playlists
    }

    suspend fun retrievePlaylist(id: String): Playlist? = withContext(Dispatchers.IO) {
        val playlistRef = FirebaseDatabase.getInstance().getReference("test").child("playlists").child(id)
        try {
            val snapshot = playlistRef.get().await()
            if (snapshot.exists()) {
                val playlistName = snapshot.child("playlistName").getValue(String::class.java) ?: ""
                val username = snapshot.child("username").getValue(String::class.java) ?: ""
                val imageURL = snapshot.child("imageURL").getValue(String::class.java) ?: ""
                val imageId = snapshot.child("imageId").getValue(Int::class.java)
                val games = snapshot.child("games").getValue(ArrayList<Games>()::class.java) ?: arrayListOf()

                val playlist = Playlist(id, playlistName, username, imageId, imageURL, games)
                return@withContext playlist
            }
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error fetching playlist data", e)
        }

        return@withContext null
    }

    suspend fun insertPlaylist(name: String, user: String): String = withContext(Dispatchers.IO) {
        val dbRef = FirebaseDatabase.getInstance().getReference("test")
        val playlistRef = dbRef.child("playlists")
        val userRef = dbRef.child("users")
        val newPlaylistRef = playlistRef.push() // Create a new unique key for the playlist

        try {
            // Set playlist attributes
            newPlaylistRef.child("playlistName").setValue(name)
            newPlaylistRef.child("username").setValue(user)
            newPlaylistRef.child("imageId").setValue(R.drawable.games)
            newPlaylistRef.child("imageURL").setValue(null)
            newPlaylistRef.child("games").setValue(ArrayList<Games>())

            // Update user's playlist data
            userRef.child(user).child("playlists").orderByKey().limitToLast(1)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.hasChildren()) {
                            val lastKey = snapshot.children.last().key?.toIntOrNull() ?: 0
                            val newKey = lastKey + 1
                            userRef.child(user).child("playlists").child(newKey.toString())
                                .setValue(newPlaylistRef.key.toString())
                        } else {
                            userRef.child(user).child("playlists").child("1")
                                .setValue(newPlaylistRef.key.toString())
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle error
                    }
                })
            return@withContext newPlaylistRef.key.toString()
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error inserting playlist", e)
        }

        return@withContext "error"
    }

    suspend fun removePlaylist(playlistId: String, username: String): Boolean = withContext(Dispatchers.IO) {
        val dbRef = FirebaseDatabase.getInstance().getReference("test")
        val playlistRef = dbRef.child("playlists").child(playlistId)
        val userRef = dbRef.child("users").child(username).child("playlists")

        try {
            playlistRef.removeValue().await()

            val userPlaylistsSnapshot = userRef.get().await()
            val playlistToRemove = userPlaylistsSnapshot.children.find { it.value == playlistId }
            playlistToRemove?.key?.let {
                userRef.child(it).removeValue().await()
            }

            return@withContext true
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error removing playlist", e)
            return@withContext false
        }
    }

    suspend fun updatePlaylistImage(playlistId: String, newImageId: String): Boolean = withContext(Dispatchers.IO) {
        val dbRef = FirebaseDatabase.getInstance().getReference("test")
        val playlistRef = dbRef.child("playlists").child(playlistId)

        try {
            playlistRef.child("imageURL").setValue(newImageId).await()
            return@withContext true
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error updating playlist image", e)
            return@withContext false
        }
    }

    suspend fun updatePlaylistName(playlistId: String, newPlaylistName: String): Boolean = withContext(Dispatchers.IO) {
        val dbRef = FirebaseDatabase.getInstance().getReference("test")
        val playlistRef = dbRef.child("playlists").child(playlistId)

        try {
            // Update the playlistName for the playlist
            playlistRef.child("playlistName").setValue(newPlaylistName).await()
            return@withContext true
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error updating playlist name", e)
            return@withContext false
        }
    }

    suspend fun addGameToPlaylist(playlistId: String, game: Games) = withContext(Dispatchers.IO) {
        val dbRef = FirebaseDatabase.getInstance().getReference("test")
        val playlistRef = dbRef.child("playlists").child(playlistId).child("games")

        try {
            val gameRef = playlistRef.push()
            gameRef.setValue(game)
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error adding game to playlist", e)
        }
    }

    suspend fun removeGameFromPlaylist(playlistId: String, game: Games) = withContext(Dispatchers.IO) {
        val dbRef = FirebaseDatabase.getInstance().getReference("test")
        val playlistGamesRef = dbRef.child("playlists").child(playlistId).child("games")

        try {
            playlistGamesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (gameSnapshot in snapshot.children) {
                            val getGame = gameSnapshot.getValue(Games::class.java)
                            if (getGame != null && getGame.gameId == game.gameId) {
                                gameSnapshot.ref.removeValue()
                                break
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("FirebaseError", "Error removing game from playlist", databaseError.toException())
                }
            })
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error removing game from playlist", e)
        }
    }
}
