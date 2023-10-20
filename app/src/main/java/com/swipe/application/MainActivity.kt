package com.swipe.application

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Spinner
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
        val spinner: Spinner = findViewById(R.id.spinner)
        val choices = arrayOf("Personal Feed", "The Kittens")

        val adapter = CustomSpinnerAdapter(this, choices)
        spinner.adapter = adapter
    }
}
