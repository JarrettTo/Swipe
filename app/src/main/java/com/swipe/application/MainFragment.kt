package com.swipe.application

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.Button
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch


class MainFragment : Fragment(){

    private lateinit var userSession: UserSession
    private var gameList: ArrayList<Games> = arrayListOf()
    private lateinit var swipeStack: SwipeStack
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("test")
    private val playlistDataHelper = PlaylistDataHelper()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.home_page, container, false)
        userSession = UserSession(requireContext())
        val userName = userSession.userName
        val userId = userSession.userId
        val likedMessageIds = userSession.likedGameIds
        val usernameTextView: TextView = view.findViewById(R.id.home_username)
        if(userName !=""){
            usernameTextView.text = userName
        }
        Log.d("DEBUG", "Arguments: ${arguments}")
        gameList = arguments?.getSerializable("gameList") as? ArrayList<Games>  ?: arrayListOf()
        Log.d("DEBUG", "gameList in Fragment: $gameList")
        swipeStack = view.findViewById(R.id.swipeStack)
  
        val swipeAdapter = SwipeAdapter(gameList) { clickedGame ->
            val intent = Intent(requireContext(), GameDetailsActivity::class.java)
            val index = gameList.indexOfFirst { it.gameId == clickedGame.gameId }
            val gameDetailsBundle = Bundle().apply {
                putSerializable("gameDetails", gameList[index])
            }
            intent.putExtra("gameDetails", gameDetailsBundle)

            startActivity(intent)
        }

        swipeStack.adapter = swipeAdapter

        swipeStack.setSaveGame(this::saveGame)
        val spinner: Spinner = view.findViewById(R.id.spinner)
        val choices = arrayOf("Personal Feed", "The Kittens")

        val adapter = CustomSpinnerAdapter(requireContext(), choices)
        spinner.adapter = adapter
        return view
    }
    fun saveGame(games: Games){
        val likedMessageIds = userSession.likedGameIds
        Log.d("TEST:", "Game Name ${games.gameName}")
        Log.d("TEST:", "Game Id ${games.gameId}")
        userSession.addLikedGameId(games.gameId.toString())
        Log.d("TEST:", "New User Liked ${likedMessageIds}")
        myRef.child("users").child(userSession.userName!!).child("likes").setValue(userSession.likedGameIds?.toList()).addOnCompleteListener {
            Log.d("TEST:", "SUCCESS!")
        }

        addGameToPlaylist(games)
    }

    private fun addGameToPlaylist(game: Games) {
        lifecycleScope.launch {
            val existingPlaylists = playlistDataHelper.retrievePlaylists(userSession.playlist)
            val playlistExists = existingPlaylists.find { it.playlistName == "Liked Games" }
            var playlistId = ""

            if (playlistExists == null) {
                playlistId = playlistDataHelper.insertPlaylist("Liked Games", userSession.userName!!)
                userSession.addPlaylistId(playlistId)
            } else {
                playlistId = playlistExists.playlistId
            }

            GamesDataHelper.fetchSingleGameInfoSteamAPI(game.gameId, object : GamesDataHelper.Companion.GameSingleInfoCallback {
                override fun onResult(result: Games?) {
                    if (result != null) {
                        Log.d("SteamAPI", "Fetched game info: ${result.gameName}")
                        lifecycleScope.launch {
                            playlistDataHelper.addGameToPlaylist(playlistId, result)
                        }
                    } else {
                        Log.d("SteamAPI", "Could not fetch game info for game ID: ${game.gameId}")
                    }
                }
            })
        }
    }

}
