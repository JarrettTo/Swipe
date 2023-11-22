package com.swipe.application
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
    private val groupDataHelper = GroupDataHelper()
    private var playlistDetails: Playlist? = null
    private var groupDetails: Groups? = null
    private lateinit var db : DatabaseHelper

    override fun onAddPlaylistGameAction(game: Games) {
        addGameToPlaylist(game)
    }

    override fun onDeletePlaylistGameAction(game: Games) {
    }

    override fun onAddPlaylistAction(playlist: Playlist) {
    }
    override fun onDeletePlaylistAction(playlist: Playlist) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search)

        playlistDetails = (intent.getBundleExtra("playlistDetails")?.getSerializable("playlistDetails") as? Playlist)
        groupDetails = (intent.getBundleExtra("groupDetails")?.getSerializable("groupDetails") as? Groups)

        val data = intent.getStringExtra("key")

        db = DatabaseHelper(this)
        if (gameList.isEmpty()) {
            Log.d("CHECK DB", "${db.getGames()}")
            gameList = db.getGames()!!
        }

        gamesListView = findViewById(R.id.list_recycler_view)
        searchView = findViewById(R.id.search_games)

        val gamesNamesList = gameList.map { it.gameName }

        gamesListView.layoutManager = LinearLayoutManager(this)

        adapter = SearchAdapter(gamesNamesList) { clickedGameName ->
            val index = gameList?.indexOfFirst { it.gameName == clickedGameName }
            if (index != null) {
                gameList?.get(index)?.let { showAddConfirmationDialog(it) }
            }
        }
        gamesListView.adapter = adapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    val dbHelper = DatabaseHelper(this@AddGamesToPlaylist)
                    val filteredGames = dbHelper.searchGamesByName(it)

                    val gameNames = filteredGames.map { game -> game.gameName.orEmpty() }
                    adapter.filterList(gameNames)
                } ?: adapter.filterList(emptyList())
                return false
            }
        })
    }

    private fun showAddConfirmationDialog(game: Games) {
        val dialogView = layoutInflater.inflate(R.layout.remove_prompt, null)
        val tvConfirmMessage = dialogView.findViewById<TextView>(R.id.tvDeleteConfirm)
        tvConfirmMessage.text = "Are you sure you want to add ${game.gameName}?"

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)

        val alertDialog = dialogBuilder.create()

        dialogView.findViewById<Button>(R.id.btnConfirmYes).setOnClickListener {
            val returnIntent = Intent().apply {
                putExtra("returnedGame", game)
            }

            setResult(Activity.RESULT_OK, returnIntent)
            alertDialog.dismiss()
            finish()
        }

        dialogView.findViewById<Button>(R.id.btnConfirmNo).setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun addGameToPlaylist(game: Games) {
        GamesDataHelper.fetchSingleGameInfoSteamAPI(game.gameId, object : GamesDataHelper.Companion.GameSingleInfoCallback {
            override fun onResult(result: Games?) {
                if (result != null) {
                    Log.d("SteamAPI", "Fetched game info: ${result.gameName}")
                    lifecycleScope.launch {
                        playlistDetails?.let { playlistDataHelper.addGameToPlaylist(it.playlistId, result) }
                    }
                } else {
                    Log.d("SteamAPI", "Could not fetch game info for game ID: ${game.gameId}")
                }
            }
        })
    }
}
