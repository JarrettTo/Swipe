package com.swipe.application
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
class GroupFragment : Fragment() {
    private lateinit var groupView: RecyclerView

    private lateinit var userSession: UserSession
    private lateinit var groupList: ArrayList<Groups>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.groups, container, false)
        userSession = UserSession(requireContext())
        val groups = userSession.groups
        groupView = view.findViewById(R.id.recycleGroup)
        val layoutManager = LinearLayoutManager(requireContext())
        groupView.layoutManager = layoutManager
        Log.d("GROUPIDS:", "CHECK ${groups}")
        lifecycleScope.launch {
            val groupList = DataHelper.retrieveGroups(groups)
            Log.d("GROUP:", "CHECK ${groupList}")
            val adapter = GroupAdapter(groupList)
            groupView.adapter = adapter
            // Now 'groupsList' contains your data
            // Update your UI here, e.g., set the data to an adapter
        }



        // Pass a listener to the adapter to handle item clicks



        return view
    }
}
