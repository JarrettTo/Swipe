package com.swipe.application

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LibraryActivity: AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LibraryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.library)



        val gamesList = intent.getBundleExtra("allGames")?.getSerializable("allGames") as? ArrayList<Games>

        recyclerView = findViewById(R.id.LibraryRecycler)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        //adapter = LibraryAdapter(gamesList)
        adapter = gamesList?.let { LibraryAdapter(it) }!!

        recyclerView.adapter = adapter

    }
}