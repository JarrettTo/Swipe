package com.swipe.application

class DataHelper {
    companion object {
        fun initializeData(): ArrayList<Games> {
            val data = ArrayList<Games>()
            data.add(
                Games(
                    1,
                    R.drawable.lol,
                    "League of Legends",
                    "League of Legends, commonly referred to as League, is a 2009 multiplayer online battle arena video game developed and published by Riot Games. Inspired by Defense of the Ancients, a custom map for Warcraft III, Riot's founders sought to develop a stand-alone game in the same genre.",
                    arrayListOf("MOBA", "ARPG"),
                    arrayListOf("Windows", "Mac"),
                    "Free",
                    R.raw.video1
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
                    R.raw.video2
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
                    R.raw.video3
                )
            )

            data.shuffle()

            return data;
        }
    }
}
