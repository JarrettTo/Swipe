package com.swipe.application

import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GameOrUserHolder(itemView: View, private val clickListener: (() -> Unit)? = null) : RecyclerView.ViewHolder(itemView) {
    private val icon: ImageView = itemView.findViewById(R.id.icon)
    private val name: TextView = itemView.findViewById(R.id.name)

    fun bindData(user: Users) {
        icon.setImageResource(user.profile)
        name.text = user.username
    }

    fun bindData(game: Games) {
        icon.setImageResource(game.imageId)
        name.text = game.gameName

        clickListener?.let { listener ->
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, GameDetailsActivity::class.java).apply {
                    putExtra("gameDetails", game)
                }
                itemView.context.startActivity(intent)
            }
        }
    }
}
