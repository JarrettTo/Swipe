package com.swipe.application

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView

class GameDetailsAdapter(private val mData: Games, private val lifecycleScope: LifecycleCoroutineScope, private val listener: GameDetailsListener) : RecyclerView.Adapter<GameDetailsHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameDetailsHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.see_more, parent, false)
        return GameDetailsHolder(itemView, itemView.context, lifecycleScope, listener)
    }

    override fun onBindViewHolder(holder: GameDetailsHolder, position: Int) {
        holder.bindData(mData)
    }

    override fun getItemCount(): Int {
        return 1
    }
}
