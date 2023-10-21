package com.swipe.application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GroupFragment : Fragment() {
    private lateinit var groupView: RecyclerView
    private lateinit var adapter: GroupAdapter
    private val groupList: ArrayList<Groups> = DataHelper.initializeGroups()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.groups, container, false)

        groupView = view.findViewById(R.id.recycleGroup)
        val layoutManager = LinearLayoutManager(requireContext())
        groupView.layoutManager = layoutManager

        // Pass a listener to the adapter to handle item clicks
        adapter = GroupAdapter(groupList)
        groupView.adapter = adapter

        return view
    }
}
