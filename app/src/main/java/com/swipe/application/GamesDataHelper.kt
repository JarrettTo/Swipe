package com.swipe.application

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.google.gson.stream.JsonReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import java.net.URL
import java.util.concurrent.Semaphore
import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

class GamesDataHelper : ViewModel() {
    suspend fun fetchGamesFromSteamAPI() {
        withContext(Dispatchers.IO) {
            var httpURLConnection: HttpURLConnection? = null
            try {
                val url = URL("https://api.steampowered.com/ISteamApps/GetAppList/v0002/?format=json")
                httpURLConnection = url.openConnection() as HttpURLConnection
                httpURLConnection.requestMethod = "GET"
                httpURLConnection.connect()

                if (httpURLConnection.responseCode == HttpURLConnection.HTTP_OK) {
                    JsonReader(InputStreamReader(httpURLConnection.inputStream, "UTF-8")).use { reader ->
                        reader.beginObject() // Start processing the JSON object
                        while (reader.hasNext()) {
                            if (reader.nextName() == "applist") {
                                reader.beginObject()
                                while (reader.hasNext()) {
                                    if (reader.nextName() == "apps") {
                                        reader.beginArray()
                                        while (reader.hasNext()) {
                                            reader.beginObject()
                                            var appId = 0
                                            var name = ""
                                            while (reader.hasNext()) {
                                                when (reader.nextName()) {
                                                    "appid" -> appId = reader.nextInt()
                                                    "name" -> name = reader.nextString()
                                                    else -> reader.skipValue()
                                                }
                                            }
                                            gameIds.add(appId)
                                            reader.endObject()
                                        }
                                        reader.endArray()
                                    } else {
                                        reader.skipValue()
                                    }
                                }
                                reader.endObject()
                            } else {
                                reader.skipValue()
                            }
                        }
                        reader.endObject()
                    }
                    Log.d("GAME:", "I'm being called AAAAAAAAAAAAA")
                } else {
                    // Handle error response...
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle the exception...
            } finally {
                httpURLConnection?.disconnect()
            }
        }
    }
    companion object {
        val gameIds: ArrayList<Int> = ArrayList()
        lateinit var retrievedGame : Games
        var index = 0

        // ...
        interface GameInfoCallback {
            fun onResult(result: Boolean)
        }
        interface GameSingleInfoCallback {
            fun onResult(result: Games?)
        }

        /*suspend fun retrieveGames(end: Int, likedGames: MutableSet<String>): List<Games> {
            val gameArray: MutableList<Games> = mutableListOf()
            val deferredGames: MutableList<Deferred<Games?>> = mutableListOf()
            val semaphore = Semaphore(5) // Semaphore with 10 permits
            val coroutineScope = CoroutineScope(Dispatchers.IO)

            coroutineScope.launch {
                var currentIndex = index
                while (gameArray.size < end && currentIndex < gameIds.size) {
                    val gameId = gameIds[currentIndex++]

                    if (!likedGames.contains(gameId.toString())) {
                        semaphore.acquire() // Acquire a permit before launching the async task
                        val deferred = async {
                            try {
                                fetchGameInfoSteamAPI(gameId)
                            } finally {
                                semaphore.release() // Release the permit after the task is completed
                            }
                        }
                        deferredGames.add(deferred)
                    }

                    // Process deferred results in batches or one by one
                    if (deferredGames.size >= end || currentIndex == gameIds.size) {
                        deferredGames.forEach { deferred ->
                            val game = deferred.await()
                            if (game != null && gameArray.size < end) {
                                gameArray.add(game)
                            }
                        }
                        deferredGames.clear() // Clear the processed deferreds
                    }
                }
            }.join() // Wait for the coroutine to complete

            return gameArray
        }*/




        suspend fun fetchGames(count: Int, likedGameIds: List<String>): List<Games> = withContext(Dispatchers.IO) {
            var httpURLConnection: HttpURLConnection? = null
            val gamesList = mutableListOf<Games>()

            try {
                val url = URL("http://10.0.2.2:5000/get_games?count=$count&likedGameIds=$likedGameIds") // Replace with your actual API URL
                httpURLConnection = url.openConnection() as HttpURLConnection
                httpURLConnection.requestMethod = "GET"
                httpURLConnection.connect()

                val responseCode = httpURLConnection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = httpURLConnection.inputStream.bufferedReader().use { it.readText() }
                    val jsonResponse = JSONObject(response)
                    val gamesArray = jsonResponse.getJSONArray("output")

                    for (i in 0 until gamesArray.length()) {
                        val gameJson = gamesArray.getJSONObject(i)
                        val name = gameJson.getString("name")
                        val description = gameJson.getString("description")
                        val genres = gameJson.getString("genres").split(", ").toList()
                        val headerImage = gameJson.getString("header_image")
                        val id = gameJson.getInt("id")
                        val platforms = gameJson.getString("platforms").split(", ").toList()
                        val price = gameJson.getString("price")
                        val videoUrl = gameJson.getString("video_url")

                        val game = Games(
                            id,
                            0,// other properties as per your Games class definition
                            headerImage,
                            name,
                            description,
                            ArrayList(genres),
                            ArrayList(platforms),
                            price,
                            0,// ... any other properties that need to be set
                            videoUrl
                        )
                        gamesList.add(game)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                httpURLConnection?.disconnect()
            }

            return@withContext gamesList
        }



        suspend fun retrieveUserGames(userName: String?) : MutableSet<String>? = withContext(Dispatchers.IO) {
            val dbRef = FirebaseDatabase.getInstance().getReference("test")
            val userRef = dbRef.child("users").child(userName!!).child("likes")
            val likes = mutableSetOf<String>()
            try {
                Log.d("TEST:", "CHECK ")
                val snapshot = userRef.get().await()
                if (snapshot.exists()) {
                    for (likeSnapshot in snapshot.children) {
                        likeSnapshot.getValue(String::class.java)?.let { likeId ->
                            likes.add(likeId)
                        }
                    }
                }
                return@withContext likes
            } catch (e: Exception) {
                // Handle exceptions
                Log.e("FirebaseError", "Error fetching data", e)
            }
            return@withContext null





        }
        suspend fun retrieveUserPlaylists(userName: String?) : MutableSet<String>? = withContext(Dispatchers.IO) {
            val dbRef = FirebaseDatabase.getInstance().getReference("test")
            val userRef = dbRef.child("users").child(userName!!).child("playlists")
            val playlists = mutableSetOf<String>()
            try {
                Log.d("TEST:", "CHECK ")
                val snapshot = userRef.get().await()
                if (snapshot.exists()) {
                    for (playlistSnapshot in snapshot.children) {
                        playlistSnapshot.getValue(String::class.java)?.let { playlistId ->
                            playlists.add(playlistId)
                        }
                    }
                }
                return@withContext playlists
            } catch (e: Exception) {
                // Handle exceptions
                Log.e("FirebaseError", "Error fetching data", e)
            }
            return@withContext null





        }

    }
}