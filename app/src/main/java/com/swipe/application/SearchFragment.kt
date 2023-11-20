package com.swipe.application

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchFragment : Fragment() {
    private var gameList: ArrayList<Games> = arrayListOf()
    private lateinit var gamesListView: RecyclerView
    private lateinit var adapter: SearchAdapter
    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.search, container, false)
        if (gameList.isEmpty()) {
            gameList = DataHelper().initializeData()
        }

        Log.d("SearchActivity", "All Games: $gameList")

        gamesListView = view.findViewById(R.id.list_recycler_view)
        searchView = view.findViewById(R.id.search_games)

        val gamesNamesList = gameList?.map { it.gameName }
        Log.d("SearchActivity", "Games Names List: $gamesNamesList")

        val layoutManager = LinearLayoutManager(requireContext())
        gamesListView.layoutManager = layoutManager

        // Pass a listener to the adapter to handle item clicks
        adapter = SearchAdapter(gamesNamesList.orEmpty().filterNotNull()) { clickedGameName ->
            val intent = Intent(requireContext(), GameDetailsActivity::class.java)
            val index = gameList?.indexOfFirst { it.gameName == clickedGameName }
            val gameDetailsBundle = Bundle().apply {
                putSerializable("gameDetails", index?.let { gameList?.get(it) })
            }
            intent.putExtra("gameDetails", gameDetailsBundle)

            startActivity(intent)
        }
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
        return view
    }
}
