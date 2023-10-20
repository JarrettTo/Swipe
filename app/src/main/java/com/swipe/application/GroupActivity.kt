package com.swipe.application

import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog

class GroupActivity : AppCompatActivity() {
    private lateinit var groupView: RecyclerView
    private lateinit var adapter: GroupAdapter
    private lateinit var groupViewHolder: SearchView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.groups)

        val groupList = intent.getBundleExtra("allGroups")?.getSerializable("allGroups") as? ArrayList<Groups>


        groupView = findViewById(R.id.recycleGroup)
        val layoutManager = LinearLayoutManager(this)
        groupView.layoutManager = layoutManager

        // Pass a listener to the adapter to handle item clicks
        adapter = GroupAdapter(groupList)

        groupView.adapter = adapter


    }
}