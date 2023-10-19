package com.swipe.application

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class GameDetailsAdapter(private val mData: Games) : RecyclerView.Adapter<GameDetailsHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameDetailsHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.see_more, parent, false)
        return GameDetailsHolder(itemView)
    }

    override fun onBindViewHolder(holder: GameDetailsHolder, position: Int) {
        holder.bindData(mData)
    }

    override fun getItemCount(): Int {
        return 1
    }
}
