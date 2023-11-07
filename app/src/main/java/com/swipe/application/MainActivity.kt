package com.swipe.application

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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
        replaceFragment(MainFragment())
        val homeButton: Button = findViewById(R.id.home_button)
        homeButton.setOnClickListener {
            replaceFragment(MainFragment())
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
            replaceFragment(LoginFragment())
        }



    }
    private fun replaceFragment(fragment : Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()

    }
}
