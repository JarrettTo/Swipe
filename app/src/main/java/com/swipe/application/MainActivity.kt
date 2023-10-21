package com.swipe.application

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Spinner
import android.widget.Button

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {


    private var gameList: ArrayList<Games> = arrayListOf()
    private val groupList: ArrayList<Groups> = DataHelper.initializeGroups()
    private lateinit var swipeStack: SwipeStack

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (gameList.isEmpty()) {
            gameList = DataHelper.initializeData()
        }

        val swipeStack: SwipeStack = findViewById(R.id.swipeStack)

        val swipeAdapter = SwipeAdapter(gameList) { clickedGame ->
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
        val groupsButton: Button = findViewById(R.id.groups_button)
        groupsButton.setOnClickListener {
            val intent = Intent(this@MainActivity, GroupActivity::class.java)
            val groupBundle = Bundle().apply {
                putSerializable("allGroups", groupList)
            }
            intent.putExtra("allGroups", groupBundle)
            startActivity(intent)
        }

        val libraryButton: Button = findViewById(R.id.library_button)
        libraryButton.setOnClickListener {
            val intent = Intent(this@MainActivity, LibraryActivity::class.java)
            val gameDetailsBundle = Bundle().apply {
                putSerializable("allGames", gameList)
            }
            intent.putExtra("allGames", gameDetailsBundle)
            startActivity(intent)
        }


        val profileButton: Button = findViewById(R.id.user_button)
        profileButton.setOnClickListener {
            val intent = Intent(this@MainActivity, Login::class.java)
            startActivity(intent)
        }

        swipeStack.adapter = swipeAdapter
        val spinner: Spinner = findViewById(R.id.spinner)
        val choices = arrayOf("Personal Feed", "The Kittens")

        val adapter = CustomSpinnerAdapter(this, choices)
        spinner.adapter = adapter

    }
}
