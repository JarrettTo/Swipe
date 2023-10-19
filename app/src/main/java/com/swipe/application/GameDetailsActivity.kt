package com.swipe.application

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GameDetailsActivity : AppCompatActivity() {
    private lateinit var myAdapter: GameDetailsAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.see_more)

        val gameDetails = intent.getBundleExtra("gameDetails")?.getSerializable("gameDetails") as? Games
        Log.d("MainActivity", "All Games: $gameDetails")

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        myAdapter = gameDetails?.let { GameDetailsAdapter(it) }!!
        recyclerView.adapter = myAdapter
    }
}
