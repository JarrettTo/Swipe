package com.swipe.application

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class LibraryAdapter (private var data: ArrayList<Playlist>, private val listener: PlaylistActionListener): RecyclerView.Adapter<LibraryHolder>(){
    var isNotDeleteMode = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.show_playlist, parent, false)
        return LibraryHolder(view, parent.context, listener){}
    }
    override fun getItemCount(): Int{
        return data!!.size
    }

    override fun onBindViewHolder(holder: LibraryHolder, position: Int) {
        holder.bindData(data[position], isNotDeleteMode)
    }

    fun addPlaylist(playlist: Playlist) {
        data?.add(playlist)
    }

    fun delPlaylist(playlist: Playlist) {
        data?.remove(playlist)
    }

    fun updateData(newData: ArrayList<Playlist>) {
        Log.d("LibraryAdapter", "Updating data: $newData")
        this.data.clear()
        this.data.addAll(newData)
    }
}