package com.swipe.application
import android.util.Log
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
class DataHelper {
    companion object {


// ...
        fun fetchGameInfoSteamAPI(id : Int){
            Thread {
                try {
                    val url = URL("https://store.steampowered.com/api/appdetails?appids=${id}")
                    val httpURLConnection = url.openConnection() as HttpURLConnection
                    httpURLConnection.requestMethod = "GET"
                    httpURLConnection.connect()

                    val responseCode = httpURLConnection.responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val response = httpURLConnection.inputStream.bufferedReader().use { it.readText() }
                        val jsonResponse = JSONObject(response)
                        val data = jsonResponse.getJSONObject(id.toString()).getJSONObject("data")



                        val description = data.getString("detailed_description")

                        val genres = data.getJSONArray("genres")
                        val genreString = ArrayList<String>()
                        for (i in 0 until genres.length()) {
                            val genreJson = genres.getJSONObject(i)
                            val name = genreJson.getString("description")
                            Log.d("TEST:","Genre desc ${name}")
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
                            Log.d("TEST:","Windows Platform")
                        }
                        if(mac){
                            platformString.add("mac")
                            Log.d("TEST:","Mac Platform")
                        }
                        if(linux){
                            platformString.add("linux")
                            Log.d("TEST:","Linux Platform")
                        }

                        val price = data.getJSONObject("price_overview")
                        val formatted = price.getString("final_formatted")
                            // Create a Games object. You'll need to fill in the details according to your Games class constructor.


                        Log.d("TEST:","Game price ${formatted}")

                            // Create a Games object. You'll need to fill in the details according to your Games class constructor.



                        // Now 'data' contains all the games fetched from the API
                        // You might want to update the UI on the main thread, for example:

                    } else {
                        // Handle error response...
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Handle the exception...
                }
            }.start()
        }
        fun fetchGamesFromSteamAPI() {
            Thread {
                try {
                    val url = URL("https://api.steampowered.com/ISteamApps/GetAppList/v0002/?format=json")
                    val httpURLConnection = url.openConnection() as HttpURLConnection
                    httpURLConnection.requestMethod = "GET"
                    httpURLConnection.connect()

                    val responseCode = httpURLConnection.responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val response = httpURLConnection.inputStream.bufferedReader().use { it.readText() }
                        val jsonResponse = JSONObject(response)
                        val gamesArray = jsonResponse.getJSONObject("applist").getJSONArray("apps")

                        for (i in 0 until gamesArray.length()) {
                            val gameJson = gamesArray.getJSONObject(i)
                            val name = gameJson.getString("name")
                            val appId = gameJson.getInt("appid")
                            Log.d("TEST:","Game Name ${name}")
                            Log.d("TEST:","Game Id ${appId}")
                            // Create a Games object. You'll need to fill in the details according to your Games class constructor.
                            fetchGameInfoSteamAPI(appId)
                        }


                        // Now 'data' contains all the games fetched from the API
                        // You might want to update the UI on the main thread, for example:

                    } else {
                        // Handle error response...
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Handle the exception...
                }
            }.start()
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
                    "League of Legends",
                    "League of Legends, commonly referred to as League, is a 2009 multiplayer online battle arena video game developed and published by Riot Games. Inspired by Defense of the Ancients, a custom map for Warcraft III, Riot's founders sought to develop a stand-alone game in the same genre.",
                    arrayListOf("MOBA", "ARPG", "Action Role-Playing Game"),
                    arrayListOf("Windows", "Mac"),
                    "Free",
                    R.raw.video1,
                    null,
                    arrayListOf(users[0], users[1]),
                    arrayListOf(reviews[0], reviews[1])
                )
            )

            data.add(
                Games(
                    2,
                    R.drawable.mlbb,
                    "Mobile Legends: Bang Bang",
                    "Mobile Legends: Bang Bang is a mobile multiplayer online battle arena game developed and published by Moonton, a subsidiary of ByteDance. Released in 2016, the game grew in popularity; most prominently in Southeast Asia.",
                    arrayListOf("MOBA", "ARPG"),
                    arrayListOf("Windows", "Mac", "Linux"),
                    "Free",
                    R.raw.video2,
                    arrayListOf(data[0]),
                    arrayListOf(users[2]),
                    arrayListOf(reviews[2])
                )
            )

            data.add(
                Games(
                    3,
                    R.drawable.starcraft,
                    "StarCraft II: Wings of Liberty",
                    "StarCraft II: Wings of Liberty is a science fiction real-time strategy video game developed and published by Blizzard Entertainment. It was released worldwide in July 2010 for Microsoft Windows and Mac OS X. ",
                    arrayListOf("RTS", "Action", "Adventure"),
                    arrayListOf("Windows", "Mac"),
                    "Free",
                    R.raw.video3,
                    arrayListOf(data[0], data[1]),
                    arrayListOf(users[3]),
                    null
                )
            )

            data.add(
                Games(
                    4,
                    R.drawable.starcraft,
                    "Temp Game 1",
                    "Temp",
                    arrayListOf("RTS", "Action", "Adventure"),
                    arrayListOf("Windows", "Mac"),
                    "Free",
                    R.raw.video3,
                    arrayListOf(data[0], data[1]),
                    arrayListOf(users[3]),
                    null
                )
            )

            data.add(
                Games(
                    5,
                    R.drawable.starcraft,
                    "Temp Game 2",
                    "Temp",
                    arrayListOf("RTS", "Action", "Adventure"),
                    arrayListOf("Windows", "Mac"),
                    "Free",
                    R.raw.video3,
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
