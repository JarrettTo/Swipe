package com.swipe.application

import android.content.Context
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LibraryHolder (itemView: View, private val context: Context, private val clickListener: (() -> Unit)? = null): RecyclerView.ViewHolder(itemView){
    private val gameLogo: ImageView = itemView.findViewById(R.id.icon)
    private val gameTitle: TextView = itemView.findViewById(R.id.name)

    fun bindData(games: Games){
        gameLogo.setImageResource(games.imageId)
        gameTitle.text = games.gameName

        clickListener?.let { listener ->
            itemView.setOnClickListener {
                val intent = Intent(context, GameDetailsActivity::class.java)
                val gameDetailsBundle = Bundle().apply {
                    putSerializable("gameDetails", games)
                }
                intent.putExtra("gameDetails", gameDetailsBundle)

                context.startActivity(intent)
                listener.invoke()
            }
        }
    }


}