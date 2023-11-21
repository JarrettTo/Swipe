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
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import java.net.URL
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

        suspend fun retrieveGames(end: Int, likedGames: MutableSet<String>): List<Games> {
            val gameArray: MutableList<Games> = mutableListOf()
            val deferredGames: MutableList<Deferred<Games?>> = mutableListOf()

            coroutineScope {
                var currentIndex = index
                while (gameArray.size < end && currentIndex < gameIds.size) {
                    val gameId = gameIds[currentIndex++]

                    if (!likedGames.contains(gameId.toString())) {
                        val deferred = async(Dispatchers.IO) {
                            fetchGameInfoSteamAPI(gameId)
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
            }

            return gameArray
        }


        fun fetchSingleGameInfoSteamAPI(
            id: Int,

            callback: GameSingleInfoCallback
        ) {
            val thread2 = Thread {
                var httpURLConnection: HttpURLConnection? = null
                try {
                    val url =
                        URL("https://store.steampowered.com/api/appdetails?appids=${id}&fbclid=IwAR3JLhpqp1zVApoAyYn9ldO5kYA0LVI7B2Ut3tImAyfkZYupUqqzotHJdt4")
                    httpURLConnection = url.openConnection() as HttpURLConnection
                    httpURLConnection.requestMethod = "GET"
                    httpURLConnection.connect()

                    val responseCode = httpURLConnection.responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val response =
                            httpURLConnection.inputStream.bufferedReader().use { it.readText() }
                        val jsonResponse = JSONObject(response)
                        val data = jsonResponse.getJSONObject(id.toString()).getJSONObject("data")
                        val name = data.getString("name")
                        val type = data.getString("type")
                        if (type != "game") {
                            callback.onResult(null)
                            return@Thread
                        }
                        Log.d("TEST:", "Game Type ${type}")
                        val description = data.getString("detailed_description")

                        val genres = data.getJSONArray("genres")
                        val genreString = ArrayList<String>()
                        for (i in 0 until genres.length()) {
                            val genreJson = genres.getJSONObject(i)
                            val name = genreJson.getString("description")

                            genreString.add(name)
                            // Create a Games object. You'll need to fill in the details according to your Games class constructor.

                        }
                        val platforms = data.getJSONObject("platforms")
                        val platformString = ArrayList<String>()
                        val windows = platforms.getBoolean("windows")
                        val mac = platforms.getBoolean("mac")
                        val linux = platforms.getBoolean("linux")
                        if (windows) {
                            platformString.add("windows")

                        }
                        if (mac) {
                            platformString.add("mac")

                        }
                        if (linux) {
                            platformString.add("linux")

                        }

                        val price = data.getJSONObject("price_overview")
                        val formatted = price.getString("final_formatted")
                        // Create a Games object. You'll need to fill in the details according to your Games class constructor.

                        val headerImage = data.getString("header_image")
                        val movies = data.getJSONArray("movies")
                        val videoIndex = movies.getJSONObject(0)
                        val videoJson = videoIndex.getJSONObject("mp4")
                        var video = videoJson.getString("480")
                        video = video.replace("http", "https")
                        // Create a Games object. You'll need to fill in the details according to your Games class constructor.
                        val newGame = Games(
                            id,
                            0,
                            headerImage,
                            name,
                            description,
                            genreString,
                            platformString,
                            formatted,
                            0,
                            video,
                            null,
                            null,
                            null
                        )

                        if (type == "game") {
                            callback.onResult(newGame)
                            return@Thread
                        }
                        // Now 'data' contains all the games fetched from the API
                        // You might want to update the UI on the main thread, for example:

                    } else {

                        callback.onResult(null)
                        return@Thread

                        // Handle error response...
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    callback.onResult(null)
                    return@Thread
                    // Handle the exception...
                } finally {
                    httpURLConnection?.disconnect() // Ensure the connection is closed in the finally block
                }
            }
            thread2.start()
            thread2.join()

        }
        suspend fun fetchGameInfoSteamAPI(
            id: Int,

        ) : Games? = withContext(Dispatchers.IO){

            var httpURLConnection: HttpURLConnection? = null
            try {
                val url =
                    URL("https://store.steampowered.com/api/appdetails?appids=${id}&fbclid=IwAR3JLhpqp1zVApoAyYn9ldO5kYA0LVI7B2Ut3tImAyfkZYupUqqzotHJdt4")
                httpURLConnection = url.openConnection() as HttpURLConnection
                httpURLConnection.requestMethod = "GET"
                httpURLConnection.connect()

                val responseCode = httpURLConnection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response =
                        httpURLConnection.inputStream.bufferedReader().use { it.readText() }
                    val jsonResponse = JSONObject(response)
                    val data = jsonResponse.getJSONObject(id.toString()).getJSONObject("data")
                    val name = data.getString("name")
                    val type = data.getString("type")
                    if (type != "game") {

                        return@withContext null
                    }
                    Log.d("TEST:", "Game Type ${type}")
                    val description = data.getString("detailed_description")

                    val genres = data.getJSONArray("genres")
                    val genreString = ArrayList<String>()
                    for (i in 0 until genres.length()) {
                        val genreJson = genres.getJSONObject(i)
                        val name = genreJson.getString("description")

                        genreString.add(name)
                        // Create a Games object. You'll need to fill in the details according to your Games class constructor.

                    }
                    val platforms = data.getJSONObject("platforms")
                    val platformString = ArrayList<String>()
                    val windows = platforms.getBoolean("windows")
                    val mac = platforms.getBoolean("mac")
                    val linux = platforms.getBoolean("linux")
                    if (windows) {
                        platformString.add("windows")

                    }
                    if (mac) {
                        platformString.add("mac")

                    }
                    if (linux) {
                        platformString.add("linux")

                    }

                    val price = data.getJSONObject("price_overview")
                    val formatted = price.getString("final_formatted")
                    // Create a Games object. You'll need to fill in the details according to your Games class constructor.

                    val headerImage = data.getString("header_image")
                    val movies = data.getJSONArray("movies")
                    val videoIndex = movies.getJSONObject(0)
                    val videoJson = videoIndex.getJSONObject("mp4")
                    var video = videoJson.getString("480")
                    video = video.replace("http", "https")
                    // Create a Games object. You'll need to fill in the details according to your Games class constructor.
                    val newGame = Games(
                        id,
                        0,
                        headerImage,
                        name,
                        description,
                        genreString,
                        platformString,
                        formatted,
                        0,
                        video,
                        null,
                        null,
                        null
                    )

                    if (type == "game") {
                        index++
                        return@withContext newGame
                    } else {
                        return@withContext null
                    }
                    // Now 'data' contains all the games fetched from the API
                    // You might want to update the UI on the main thread, for example:

                } else {

                    return@withContext null

                    // Handle error response...
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext null
                // Handle the exception...
            } finally {
                httpURLConnection?.disconnect() // Ensure the connection is closed in the finally block
            }




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