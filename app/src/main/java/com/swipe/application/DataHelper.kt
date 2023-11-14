package com.swipe.application
import android.util.Log
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class DataHelper {

    companion object {
        val gameIds : ArrayList<Int> = ArrayList()

        var index = 0
// ...
        interface GameInfoCallback {
            fun onResult(result: Boolean)
        }
        fun retrieveGames(start : Int, end : Int) : ArrayList<Games>{
            var gameArray : ArrayList<Games> = ArrayList()
            var i = start
            var count = 0
            gameArray.clear()
            while (i<gameIds.size && count<end) {

                // Create a Games object. You'll need to fill in the details according to your Games class constructor.
                fetchGameInfoSteamAPI(gameIds[i],gameArray, object : GameInfoCallback {
                    override fun onResult(result: Boolean) {
                        if(result){
                            count+=1
                        }

                    }
                })
                i+=1
            }
            index = i
            return gameArray
        }
        fun retrieveGames(end : Int) : ArrayList<Games>{
            var gameArray : ArrayList<Games> = ArrayList()
            var i = index
            var count = 0
            gameArray.clear()
            Log.d("GAME:","I'm being called")
            while (i<gameIds.size && count<end) {

                // Create a Games object. You'll need to fill in the details according to your Games class constructor.
                fetchGameInfoSteamAPI(gameIds[i], gameArray, object : GameInfoCallback {
                    override fun onResult(result: Boolean) {
                        if(result){
                            count+=1
                        }

                    }
                })
                i+=1
            }
            index = i
            Log.d("GAME:","Game Array Len ${gameArray.size}")
            return gameArray
        }
        fun fetchGameInfoSteamAPI(id : Int, gameArray : ArrayList<Games>, callback: GameInfoCallback) {
            val thread2 = Thread {
                var httpURLConnection: HttpURLConnection? = null
                try {
                    val url = URL("https://store.steampowered.com/api/appdetails?appids=${id}&fbclid=IwAR3JLhpqp1zVApoAyYn9ldO5kYA0LVI7B2Ut3tImAyfkZYupUqqzotHJdt4")
                    httpURLConnection = url.openConnection() as HttpURLConnection
                    httpURLConnection.requestMethod = "GET"
                    httpURLConnection.connect()

                    val responseCode = httpURLConnection.responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val response = httpURLConnection.inputStream.bufferedReader().use { it.readText() }
                        val jsonResponse = JSONObject(response)
                        val data = jsonResponse.getJSONObject(id.toString()).getJSONObject("data")
                        val name = data.getString("name")
                        val type = data.getString("type")
                        if(type!="game"){
                            callback.onResult(false)
                            return@Thread
                        }
                        Log.d("TEST:","Game Type ${type}")
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
                        if(windows){
                            platformString.add("windows")

                        }
                        if(mac){
                            platformString.add("mac")

                        }
                        if(linux){
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
                        val newGame = Games(id,0, headerImage,  name,description,genreString,platformString,formatted,0,video,null,null,null)
                        gameArray.add(newGame)
                        if(type=="game"){
                            callback.onResult(true)
                            return@Thread
                        }
                        // Now 'data' contains all the games fetched from the API
                        // You might want to update the UI on the main thread, for example:

                    } else {

                        callback.onResult(false)
                        return@Thread

                        // Handle error response...
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    callback.onResult(false)
                    return@Thread
                    // Handle the exception...
                } finally {
                    httpURLConnection?.disconnect() // Ensure the connection is closed in the finally block
                }
            }
            thread2.start()
            thread2.join()
        }
        fun fetchGamesFromSteamAPI() {
            var count = 0
            val thread1 = Thread {
                var httpURLConnection: HttpURLConnection? = null
                try {

                    val url = URL("https://api.steampowered.com/ISteamApps/GetAppList/v0002/?format=json")
                    httpURLConnection = url.openConnection() as HttpURLConnection
                    httpURLConnection.requestMethod = "GET"
                    httpURLConnection.connect()

                    val responseCode = httpURLConnection.responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val response = httpURLConnection.inputStream.bufferedReader().use { it.readText() }
                        val jsonResponse = JSONObject(response)
                        val gamesArray = jsonResponse.getJSONObject("applist").getJSONArray("apps")
                        var i = 0
                        while (i<gamesArray.length()) {
                            val gameJson = gamesArray.getJSONObject(i)
                            val name = gameJson.getString("name")
                            val appId = gameJson.getInt("appid")

                            // Create a Games object. You'll need to fill in the details according to your Games class constructor.
                            /*fetchGameInfoSteamAPI(appId,(count>=index), object : GameInfoCallback {
                                override fun onResult(result: Boolean) {
                                    if(result){
                                        count+=1
                                    }

                                }
                            })*/
                            gameIds.add(appId)
                            i+=1
                        }
                        Log.d("GAME:","I'm being called AAAAAAAAAAAAA")



                        // Now 'data' contains all the games fetched from the API
                        // You might want to update the UI on the main thread, for example:

                    } else {
                        // Handle error response...
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Handle the exception...
                }finally {
                    httpURLConnection?.disconnect() // Ensure the connection is closed in the finally block
                }
            }
            thread1.start()
            thread1.join()
        }

        fun initializeData(): ArrayList<Games> {
            fetchGamesFromSteamAPI()
            val users = ArrayList<Users>()
            users.add(
                Users(
                    "Faker",
                    R.drawable.faker
                )
            )
            users.add(
                Users(
                    "bjergsen",
                    R.drawable.bjergsen
                )
            )
            users.add(
                Users(
                    "karltzy",
                    R.drawable.karltzy
                )
            )
            users.add(
                Users(
                    "Serral",
                    R.drawable.serral
                )
            )

            val reviews = ArrayList<Reviews>()
            reviews.add(
                Reviews(
                    users[0],
                    1,
                    4,
                    "Great game!"
                )
            )
            reviews.add(
                Reviews(
                    users[1],
                    1,
                    5,
                    "Amazing."
                )
            )
            reviews.add(
                Reviews(
                    users[2],
                    2,
                    3,
                    "wow!"
                )
            )

            val data = ArrayList<Games>()
            data.add(
                Games(
                    1,
                    R.drawable.lol,
                    null,
                    "League of Legends",
                    "League of Legends, commonly referred to as League, is a 2009 multiplayer online battle arena video game developed and published by Riot Games. Inspired by Defense of the Ancients, a custom map for Warcraft III, Riot's founders sought to develop a stand-alone game in the same genre.",
                    arrayListOf("MOBA", "ARPG", "Action Role-Playing Game"),
                    arrayListOf("Windows", "Mac"),
                    "Free",
                    R.raw.video1,
                    null,
                    null,
                    arrayListOf(users[0], users[1]),
                    arrayListOf(reviews[0], reviews[1])
                )
            )

            data.add(
                Games(
                    2,
                    R.drawable.mlbb,
                    null,
                    "Mobile Legends: Bang Bang",
                    "Mobile Legends: Bang Bang is a mobile multiplayer online battle arena game developed and published by Moonton, a subsidiary of ByteDance. Released in 2016, the game grew in popularity; most prominently in Southeast Asia.",
                    arrayListOf("MOBA", "ARPG"),
                    arrayListOf("Windows", "Mac", "Linux"),
                    "Free",
                    R.raw.video2,
                    null,
                    arrayListOf(data[0]),
                    arrayListOf(users[2]),
                    arrayListOf(reviews[2])
                )
            )

            data.add(
                Games(
                    3,
                    R.drawable.starcraft,
                    null,
                    "StarCraft II: Wings of Liberty",
                    "StarCraft II: Wings of Liberty is a science fiction real-time strategy video game developed and published by Blizzard Entertainment. It was released worldwide in July 2010 for Microsoft Windows and Mac OS X. ",
                    arrayListOf("RTS", "Action", "Adventure"),
                    arrayListOf("Windows", "Mac"),
                    "Free",
                    R.raw.video3,
                    null,
                    arrayListOf(data[0], data[1]),
                    arrayListOf(users[3]),
                    null
                )
            )

            data.add(
                Games(
                    4,
                    R.drawable.starcraft,
                    null,
                    "Temp Game 1",
                    "Temp",
                    arrayListOf("RTS", "Action", "Adventure"),
                    arrayListOf("Windows", "Mac"),
                    "Free",
                    R.raw.video3,
                    null,
                    arrayListOf(data[0], data[1]),
                    arrayListOf(users[3]),
                    null
                )
            )

            data.add(
                Games(
                    5,
                    R.drawable.starcraft,
                    null,
                    "Temp Game 2",
                    "Temp",
                    arrayListOf("RTS", "Action", "Adventure"),
                    arrayListOf("Windows", "Mac"),
                    "Free",
                    R.raw.video3,
                    null,
                    arrayListOf(data[0], data[1]),
                    arrayListOf(users[3]),
                    null
                )
            )

            data.shuffle()

            return data;
        }
        fun initializeGroups() : ArrayList<Groups>{
            val groups = ArrayList<Groups>()
            groups.add(
                Groups("The Kittens", R.drawable.lol, "This ggroup is made for the kittens of demacia or smth like dat", 4)
            )
            groups.add(
                Groups("I Miss You", R.drawable.bjergsen, "Relapse hours go crazy cos my love is mine all mine", 2)
            )
            groups.add(
                Groups("SHEESH ESPORTS", R.drawable.starcraft, "This group is the starcraft pro team champion", 6)
            )
            return groups
        }
    }
}
