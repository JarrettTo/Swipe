package com.swipe.application

import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {
    private lateinit var gamesListView: RecyclerView
    private lateinit var adapter: SearchAdapter
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search)

        val gameList = intent.getBundleExtra("allGames")?.getSerializable("allGames") as? List<Games>
        Log.d("SearchActivity", "All Games: $gameList")

        gamesListView = findViewById(R.id.list_recycler_view)
        searchView = findViewById(R.id.search_games)

        val gamesNamesList = gameList?.map { it.gameName }
        Log.d("SearchActivity", "Games Names List: $gamesNamesList")

        val layoutManager = LinearLayoutManager(this)
        gamesListView.layoutManager = layoutManager

        adapter = SearchAdapter(gamesNamesList.orEmpty().filterNotNull())
        gamesListView.adapter = adapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Do nothing here
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    adapter.filterList(gamesNamesList.orEmpty().filterNotNull())
                } else {
                    val filteredList = gamesNamesList.orEmpty().filter {
                        it?.startsWith(newText, ignoreCase = true) == true
                    }.mapNotNull { it }

                    Log.d("SearchActivity", "Filtered List: $filteredList")

                    adapter.filterList(filteredList)
                }
                return false
            }
        })
    }
}
