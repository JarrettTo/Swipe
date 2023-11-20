package com.swipe.application

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

interface GameDetailsListener {
    fun onReviewUpdated()
}

class GameDetailsActivity : AppCompatActivity(), GameDetailsListener {
    private lateinit var myAdapter: GameDetailsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var backButton: Button

    override fun onReviewUpdated() {
        myAdapter.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.see_more)

        var gameDetails = intent.getBundleExtra("gameDetails")?.getSerializable("gameDetails") as? Games
        if (gameDetails == null) {
            gameDetails = intent.getSerializableExtra("gameDetails") as? Games
        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        myAdapter = gameDetails?.let { GameDetailsAdapter(it, lifecycleScope, this) }!!
        recyclerView.adapter = myAdapter

        backButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressed()
        }
    }
}
