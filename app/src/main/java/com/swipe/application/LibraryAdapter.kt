package com.swipe.application

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView

class LibraryAdapter (private val data: ArrayList<Games>): RecyclerView.Adapter<LibraryHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.game_or_user, parent, false)
        return LibraryHolder(view, parent.context)
    }
    override fun getItemCount(): Int{
        return data!!.size
    }

    override fun onBindViewHolder(holder: LibraryHolder, position: Int) {
        holder.bindData(data!![position])
    }

//    class LibraryGamesHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
//        val gameImage: ImageView = itemView.findViewById(R.id.icon)
//        val gameTitle: TextView = itemView.findViewById(R.id.name)
//        val cardView: CardView = itemView.findViewById(R.id.CardView)
//
//    }
}