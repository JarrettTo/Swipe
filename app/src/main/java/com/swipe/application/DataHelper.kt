package com.swipe.application
import android.net.Uri
import android.util.Log
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
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.net.URL
import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

class DataHelper {
    private lateinit var data: ArrayList<Games>
    private lateinit var users: ArrayList<Users>

    fun initializeData(): ArrayList<Games> {
        users = ArrayList<Users>()
        users.add(
            Users(
                "Faker",
                R.drawable.faker,
                ""
            )
        )
        users.add(
            Users(
                "bjergsen",
                R.drawable.bjergsen,
                ""
            )
        )
        users.add(
            Users(
                "karltzy",
                R.drawable.karltzy,
                ""
            )
        )
        users.add(
            Users(
                "Serral",
                R.drawable.serral,
                ""
            )
        )

        val reviews = ArrayList<Reviews>()
        reviews.add(
            Reviews(
                "1",
                users[0],
                1,
                4,
                "Great game!"
            )
        )
        reviews.add(
            Reviews(
                "2",
                users[1],
                1,
                5,
                "Amazing."
            )
        )
        reviews.add(
            Reviews(
                "3",
                users[2],
                2,
                3,
                "wow!"
            )
        )

        data = ArrayList<Games>()
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

    fun initializePlaylist() : ArrayList<Playlist>{
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
                null,
                null
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
                null,
                null
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
                null,
                null,
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
                null,
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
                null,
                null
            )
        )

        val playlist = ArrayList<Playlist>()
        playlist.add(
            Playlist("1", "Liked Games", "heheWOW23", R.drawable.games, "", data)
        )
        playlist.add(
            Playlist("2", "Faves", "heheWOW23", R.drawable.games, "", data)
        )
        playlist.add(
            Playlist("3", "Liked Games", "Aly", R.drawable.games, "", data)
        )

        return playlist
    }

    fun findGamebyName(newGame: String): Games? {
        return data.find { game -> game.gameName == newGame }
    }

    fun findGamebyID(newGame: String): Games? {
        return data.find { game -> game.gameId.toString() == newGame }
    }

    fun getUser() : Users {
        var user = Users(
            "heheWOW23",
            R.drawable.dp,
            ""
        )
        return user
    }
}
