package com.swipe.application

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GroupHolder(itemView: View, private val context: Context) : RecyclerView.ViewHolder(itemView) {
    private val banner: ImageView = itemView.findViewById(R.id.grpBanner)
    private val count: TextView = itemView.findViewById(R.id.grpCount)
    private val name: TextView = itemView.findViewById(R.id.grpName)
    private val desc: TextView = itemView.findViewById(R.id.grpDesc)
    fun bindData(group: Groups){
        banner.setImageResource(group.imageId)
        count.text = group.userCount.toString()
        name.text = group.name
        desc.text = group.description

    }
}