package com.swipe.application

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
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
    private val userDataHelper = UserDataHelper()
    private val gamesDataHelper = GamesDataHelper()
    private lateinit var progressBar: ProgressBar
    private lateinit var errorLayout: LinearLayout
    private lateinit var errorMessage: TextView
    private lateinit var retryButton: Button
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_main)
        val db = DatabaseHelper(this)
        errorLayout = findViewById<LinearLayout>(R.id.error_layout)
        errorMessage = findViewById<TextView>(R.id.error_message)
        retryButton = findViewById<Button>(R.id.retry_button)
        userSession = UserSession(this)
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        retryButton.setOnClickListener {
            errorLayout.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            fetchData() // Refactor your data fetching logic into this function
        }
        lifecycleScope.launch {
            userSession.groups= userDataHelper.retrieveUserGroups(userSession.userName)
            Log.d("GROUP FB", "DEBUG: ${userDataHelper.retrieveUserGroups(userSession.userName)}")
            userSession.likedGameIds= userDataHelper.retrieveUserGames(userSession.userName)
            Log.d("LIKES FB", "DEBUG: ${userDataHelper.retrieveUserGames(userSession.userName)}")
            userSession.playlist= userDataHelper.retrieveUserPlaylists(userSession.userName)
            Log.d("Playlists FB", "DEBUG: ${GamesDataHelper.retrieveUserPlaylists(userSession.userName)}")

            if (gameList.isEmpty()) {
                try {
                    // Assuming fetchGamesFromSteamAPI is a suspend function, otherwise it should be called normally
                    gamesDataHelper.fetchGamesFromSteamAPI()

                    fetchData()
                     // This is called from within a coroutine
                    db.saveGames(ArrayList(GamesDataHelper.fetchLikedGames(userSession.likedGameIds!!.toList())))
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    // Hide the ProgressBar once the data is loaded or an error occurs



                }
            }
        }

        if(userSession.userName == ""){
            //TODO: Insert logic that redirects them to login page
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }





        // Now you can use gameList as it's filled with data

    }
    private fun fetchData() {
        lifecycleScope.launch {
            try {
                val gameArr = GamesDataHelper.fetchGames(10, userSession.likedGameIds!!.toList())
                if(gameArr==null){
                    progressBar.visibility = View.GONE
                    errorLayout.visibility = View.VISIBLE

                }else{
                    gameList = ArrayList(gameArr)
                }
                errorLayout.visibility = View.GONE
            } catch (e: Exception) {
                progressBar.visibility = View.GONE

                errorLayout.visibility = View.VISIBLE
            } finally {

                if(gameList.isEmpty()){
                    errorLayout.visibility = View.VISIBLE
                }else{

                    progressBar.visibility = View.GONE
                    setupUI()
                }
            }
        }
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
            if (this@MainActivity is MainActivity) {
                // If in MainActivity, replace fragment
                val mf = MainFragment().apply { arguments = bundle }
                replaceFragment(mf)
            } else {
                // If not in MainActivity, start MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
             }}
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