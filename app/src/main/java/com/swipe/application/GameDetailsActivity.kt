package com.swipe.application

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GameDetailsActivity : AppCompatActivity() {
    private lateinit var myAdapter: GameDetailsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.see_more)

        val gameDetails = intent.getBundleExtra("gameDetails")?.getSerializable("gameDetails") as? Games

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        myAdapter = gameDetails?.let { GameDetailsAdapter(it) }!!
        recyclerView.adapter = myAdapter

        backButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressed()
        }
    }
}
