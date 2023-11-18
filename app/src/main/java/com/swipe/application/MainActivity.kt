package com.swipe.application

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment


class MainActivity : AppCompatActivity() {


    private var gameList: ArrayList<Games> = arrayListOf()
    private lateinit var swipeStack: SwipeStack

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (gameList.isEmpty()) {
            try{
                DataHelper.fetchGamesFromSteamAPI()
                gameList = DataHelper.retrieveGames(10)
            } catch (e: Exception){
                e.printStackTrace()
            }


        }
        val bundle = Bundle()
        bundle.putSerializable("gameList", gameList)
        Log.d("DEBUG", "LIST: ${bundle}")
        val mf = MainFragment()
        mf.arguments=bundle
        replaceFragment(mf)


        val homeButton: Button = findViewById(R.id.home_button)
        homeButton.setOnClickListener {
            val mf = MainFragment()
            mf.arguments=bundle
            replaceFragment(mf)
        }
        val searchButton: Button = findViewById(R.id.search_button)
        searchButton.setOnClickListener {
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



    }
    private fun replaceFragment(fragment : Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()

    }
}
