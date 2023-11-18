package com.swipe.application

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class LibraryAdapter (private val data: ArrayList<Playlist>): RecyclerView.Adapter<LibraryHolder>(){
    var isNotDeleteMode = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.show_playlist, parent, false)
        return LibraryHolder(view, parent.context){}
    }
    override fun getItemCount(): Int{
        return data!!.size
    }

    override fun onBindViewHolder(holder: LibraryHolder, position: Int) {
        holder.bindData(data[position], isNotDeleteMode)
    }

}