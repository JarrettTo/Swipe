package com.swipe.application

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private val gameList: ArrayList<Games> = DataHelper.initializeData()
    private lateinit var swipeStack: SwipeStack

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        swipeStack = findViewById(R.id.swipeStack)
        swipeStack.adapter = SwipeAdapter(gameList)
    }
}
