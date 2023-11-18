package com.swipe.application

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class GroupAdapter(private var data: ArrayList<Groups>?) : RecyclerView.Adapter<GroupHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.group, parent, false)
        return GroupHolder(view, parent.context)
    }


    override fun onBindViewHolder(holder: GroupHolder, position: Int) {

        holder.bindData(data!![position])

    }

    override fun getItemCount(): Int {
        return data!!.size


    }
    fun updateGroups(newGroups: ArrayList<Groups>) {
        data = newGroups
        notifyDataSetChanged() // This will refresh the RecyclerView with new data
    }

}