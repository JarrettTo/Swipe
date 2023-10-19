package com.swipe.application

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private val gameList: ArrayList<Games> = DataHelper.initializeData()
    private lateinit var swipeStack: SwipeStack

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val swipeStack: SwipeStack = findViewById(R.id.swipeStack)

        val swipeAdapter = SwipeAdapter(gameList) { clickedGame ->
            // Do something with the clicked item before going to the second activity
            Log.d("MainActivity", "Button clicked for game ID: ${clickedGame.gameId}")
            // For example, pass it to the second activity
            val intent = Intent(this@MainActivity, GameDetailsActivity::class.java)
            val index = gameList.indexOfFirst { it.gameId == clickedGame.gameId }
            val gameDetailsBundle = Bundle().apply {
                putSerializable("gameDetails", gameList[index])
            }
            intent.putExtra("gameDetails", gameDetailsBundle)

            startActivity(intent)
        }

        swipeStack.adapter = swipeAdapter
    }
}
