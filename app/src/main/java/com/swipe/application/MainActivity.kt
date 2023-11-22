package com.swipe.application

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.lifecycle.lifecycleScope
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.launch

interface GameSingleInfoCallback {
    fun onResult(result: Games?)
}
class MainActivity : AppCompatActivity() {
    private var gameList: ArrayList<Games> = arrayListOf()
    private lateinit var swipeStack: SwipeStack
    private lateinit var userSession: UserSession
    private val groupDataHelper = GroupDataHelper()
    private val gamesDataHelper = GamesDataHelper()
    private lateinit var progressBar: ProgressBar
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_main)
        userSession = UserSession(this)
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            userSession.groups= groupDataHelper.retrieveUserGroups(userSession.userName)
            Log.d("GROUP FB", "DEBUG: ${groupDataHelper.retrieveUserGroups(userSession.userName)}")
            userSession.likedGameIds= GamesDataHelper.retrieveUserGames(userSession.userName)
            Log.d("LIKES FB", "DEBUG: ${GamesDataHelper.retrieveUserGames(userSession.userName)}")
            userSession.playlist= GamesDataHelper.retrieveUserPlaylists(userSession.userName)
            Log.d("Playlists FB", "DEBUG: ${GamesDataHelper.retrieveUserPlaylists(userSession.userName)}")
            if (gameList.isEmpty()) {
                try {
                    // Assuming fetchGamesFromSteamAPI is a suspend function, otherwise it should be called normally
                    gamesDataHelper.fetchGamesFromSteamAPI()
                    gameList = ArrayList(GamesDataHelper.fetchGames(10, userSession.likedGameIds!!.toList())) // This is called from within a coroutine
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    // Hide the ProgressBar once the data is loaded or an error occurs
                    progressBar.visibility = View.GONE
                    setupUI()
                }
            }
        }

        if(userSession.userName == ""){
            //TODO: Insert logic that redirects them to login page
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        val db = DatabaseHelper(this)
        for(i in 0 until userSession.likedGameIds!!.size){
            GamesDataHelper.fetchSingleGameInfoSteamAPI(userSession.likedGameIds!!.elementAtOrNull(i)?.toInt()!!, object : GamesDataHelper.Companion.GameSingleInfoCallback {
                override fun onResult(result: Games?) {
                    if (result!=null) {
                        db.saveGame(result)
                    }
                }
            })


        }



        // Now you can use gameList as it's filled with data

    }
    private fun setupUI() {
        // Now you can use gameList as it's filled with data
        val bundle = Bundle().apply { putSerializable("gameList", gameList) }
        Log.d("Bundle:", "${bundle}")
        // Setup the initial fragment
        val mf = MainFragment().apply { arguments = bundle }
        replaceFragment(mf)

        // Setup buttons
        setupButtons(bundle)
    }
    private fun setupButtons(bundle: Bundle) {
        findViewById<Button>(R.id.home_button).setOnClickListener {
            val mf = MainFragment().apply { arguments = bundle }
            replaceFragment(mf)
        }
        findViewById<Button>(R.id.search_button).setOnClickListener {
            replaceFragment(SearchFragment())
        }
        val groupsButton: Button = findViewById(R.id.groups_button)
        groupsButton.setOnClickListener {
            replaceFragment(GroupFragment())
        }

        val libraryButton: Button = findViewById(R.id.library_button)
        libraryButton.setOnClickListener {
            replaceFragment(LibraryFragment())
        }

        val profileButton: Button = findViewById(R.id.user_button)
        profileButton.setOnClickListener {
            replaceFragment(UserProfileFragment())
        }
        //... Other button setups
    }

    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }
}