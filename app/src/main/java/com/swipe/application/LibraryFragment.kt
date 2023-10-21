package com.swipe.application

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LibraryFragment : Fragment(){
    private var gamesList: ArrayList<Games> = arrayListOf()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LibraryAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.library, container, false)



        gamesList = DataHelper.initializeData()

        recyclerView = view.findViewById(R.id.LibraryRecycler)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        //adapter = LibraryAdapter(gamesList)
        adapter = gamesList?.let { LibraryAdapter(it) }!!

        recyclerView.adapter = adapter
        return view
    }
}