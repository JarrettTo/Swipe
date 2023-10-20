package com.swipe.application

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Spinner

import android.util.Log
import android.view.View
import android.widget.Button

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

        val searchButton: Button = findViewById(R.id.search_button)
        searchButton.setOnClickListener {
            val intent = Intent(this@MainActivity, SearchActivity::class.java)
            val gameDetailsBundle = Bundle().apply {
                putSerializable("allGames", gameList)
            }
            intent.putExtra("allGames", gameDetailsBundle)
            startActivity(intent)
        }

        swipeStack.adapter = swipeAdapter
        val spinner: Spinner = findViewById(R.id.spinner)
        val choices = arrayOf("Personal Feed", "The Kittens")

        val adapter = CustomSpinnerAdapter(this, choices)
        spinner.adapter = adapter

    }
}
