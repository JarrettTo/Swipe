package com.swipe.application

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Button
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean


class MainFragment : Fragment(){

    private lateinit var userSession: UserSession
    private var gameList: ArrayList<Games> = arrayListOf()
    private lateinit var swipeStack: SwipeStack
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("test")
    private val playlistDataHelper = PlaylistDataHelper()
    private val groupDataHelper = GroupDataHelper()
    private val userDataHelper = UserDataHelper()
    private lateinit var db : DatabaseHelper
    private lateinit var feed: String
    private val isNavigatingToMain = AtomicBoolean(false)
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.home_page, container, false)
        userSession = UserSession(requireContext())
        val userName = userSession.userName

        val usernameTextView: TextView = view.findViewById(R.id.home_username)
        if (userName != "") {
            usernameTextView.text = userName
        }
        Log.d("DEBUG", "Arguments: ${arguments}")
        gameList = arguments?.getSerializable("gameList") as? ArrayList<Games> ?: arrayListOf()
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
        db = DatabaseHelper(requireContext())
        swipeStack.setSaveGame(this::saveGame)
        swipeStack.setOnSwipe(this::onSwipe)
        val spinner: Spinner = view.findViewById(R.id.spinner)
        val group = GroupDataHelper()
        lifecycleScope.launch {
            val choices = group.retrieveGroupsName(userSession.groups).toTypedArray()
            val adapter = CustomSpinnerAdapter(requireContext(), choices)
            spinner.adapter = adapter

            feed = spinner.selectedItem.toString()

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    feed = parent.getItemAtPosition(position).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
        }

        return view
    }

    fun saveGame(games: Games) {
        val likedMessageIds = userSession.likedGameIds
        Log.d("TEST:", "Game Name ${games.gameName}")
        Log.d("TEST:", "Game Id ${games.gameId}")
        userSession.addLikedGameId(games.gameId.toString())
        Log.d("TEST:", "New User Liked ${likedMessageIds}")
        myRef.child("users").child(userSession.userName!!).child("likes")
            .setValue(userSession.likedGameIds?.toList()).addOnCompleteListener {
                Log.d("TEST:", "SUCCESS!")
            }
        db.saveGame(games)
        Log.d("TEST GAME DB:", "${db.getGame(games.gameId)}")
        var gameDetails = db.getGame(games.gameId)
        if (gameDetails != null) {
            addGameToPlaylist(gameDetails, feed)
        }

    }

    fun onSwipe(count: Int, likedGames: MutableSet<String>)  {
        lifecycleScope.launch {
            try{
                val games= GamesDataHelper.fetchGames(count, likedGames.toList())!!
                swipeStack.addGames(games)
            } catch (e: Exception) {
                if(swipeStack.adapter?.count==0){
                    goToMainActivity()
                }
            }

        }
    }

    private fun goToMainActivity() {
        if (isNavigatingToMain.compareAndSet(false, true)) {
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
        }
    }



    private fun addGameToPlaylist(game: Games, name: String) {
        Log.d("FEED NAME", "$name")
        if (name == "Personal Feed") {
            lifecycleScope.launch {
                val existingPlaylists =
                    playlistDataHelper.retrievePlaylists(userSession.playlist)
                val playlistExists = existingPlaylists.find { it.playlistName == "Liked Games" }
                var playlistId = ""

                if (playlistExists == null) {
                    playlistId =
                        playlistDataHelper.insertPlaylist("Liked Games", userSession.userName!!)
                    userSession.addPlaylistId(playlistId)
                } else {
                    playlistId = playlistExists.playlistId
                }

                if (!playlistDataHelper.isGameAlreadyInPlaylist(playlistId, game)) {
                    playlistDataHelper.addGameToPlaylist(playlistId, game)
                }
            }
        } else {
            lifecycleScope.launch {
                val existingGroupCode =
                    userDataHelper.retrieveUserGroups(userSession.userName)
                val existingGroups = groupDataHelper.retrieveGroups(existingGroupCode)
                val groupExists = existingGroups.find { it.name == name }
                var groupId = groupExists?.id

                Log.d("code", "$existingGroupCode + $existingGroups + $groupExists + $groupId")
                if (groupId != null && !groupDataHelper.isGameAlreadyInGroup(groupId, game)) {
                    groupDataHelper.addGameToGroup(groupId, game)
                }
            }
        }
    }

}