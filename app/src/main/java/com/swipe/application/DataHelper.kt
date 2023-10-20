package com.swipe.application

class DataHelper {
    companion object {
        fun initializeData(): ArrayList<Games> {
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
    }
}
