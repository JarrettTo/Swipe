package com.swipe.application
import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class AddGamesToPlaylist : AppCompatActivity() , PlaylistGameActionListener {
    private var gameList: ArrayList<Games> = arrayListOf()
    private lateinit var gamesListView: RecyclerView
    private lateinit var adapter: SearchAdapter
    private lateinit var searchView: SearchView
    private val playlistDataHelper = PlaylistDataHelper()
    private lateinit var playlistDetails: Playlist

    override fun onAddPlaylistGameAction(game: Games) {
        addGameToPlaylist(game)
    }

    override fun onDeletePlaylistGameAction(game: Games) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search)

        playlistDetails = (intent.getBundleExtra("playlistDetails")?.getSerializable("playlistDetails") as? Playlist)!!

        val data = intent.getStringExtra("key")

        if (gameList.isEmpty()) {
            gameList = DataHelper().initializeData()
        }

        gamesListView = findViewById(R.id.list_recycler_view)
        searchView = findViewById(R.id.search_games)

        val gamesNamesList = gameList.map { it.gameName }

        gamesListView.layoutManager = LinearLayoutManager(this)

        // Pass a listener to the adapter to handle item clicks
        adapter = SearchAdapter(gamesNamesList) { clickedGameName ->
            showAddConfirmationDialog(clickedGameName)
        }
        gamesListView.adapter = adapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredList = if (newText.isNullOrEmpty()) {
                    gamesNamesList
                } else {
                    gamesNamesList.filter { it?.startsWith(newText, ignoreCase = true) ?:  false}
                }
                adapter.filterList(filteredList)
                return false
            }
        })
    }

    private fun showAddConfirmationDialog(itemTitle: String) {
        val dialogView = layoutInflater.inflate(R.layout.remove_prompt, null)
        val tvConfirmMessage = dialogView.findViewById<TextView>(R.id.tvDeleteConfirm)
        tvConfirmMessage.text = "Are you sure you want to add $itemTitle?"

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)

        val alertDialog = dialogBuilder.create()

        dialogView.findViewById<Button>(R.id.btnConfirmYes).setOnClickListener {
            // Handle "Yes" action
            val game = DataHelper().findGamebyName(itemTitle)

            alertDialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnConfirmNo).setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun addGameToPlaylist(game: Games) {
        lifecycleScope.launch {
            playlistDataHelper.addGameToPlaylist(playlistDetails.playlistId, game)
        }
    }
}
