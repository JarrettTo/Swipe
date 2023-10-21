package com.swipe.application

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.Button

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment


class MainFragment : Fragment() {


    private var gameList: ArrayList<Games> = arrayListOf()
    private lateinit var swipeStack: SwipeStack

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.home_page, container, false)


        if (gameList.isEmpty()) {
            gameList = DataHelper.initializeData()
        }

        val swipeStack: SwipeStack = view.findViewById(R.id.swipeStack)
  
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
        val spinner: Spinner = view.findViewById(R.id.spinner)
        val choices = arrayOf("Personal Feed", "The Kittens")

        val adapter = CustomSpinnerAdapter(requireContext(), choices)
        spinner.adapter = adapter
        return view
    }
}
