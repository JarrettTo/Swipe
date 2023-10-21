package com.swipe.application

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

fun Int.dpToPx(context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        toFloat(),
        context.resources.displayMetrics
    ).toInt()
}

class LibraryHolder(itemView: View, private val context: Context, private val clickListener: (() -> Unit)? = null) :
    RecyclerView.ViewHolder(itemView) {
    private val gameLogo: ImageView = itemView.findViewById(R.id.icon)
    private val gameTitle: TextView = itemView.findViewById(R.id.name)

    fun bindData(games: Games) {
        gameLogo.setImageResource(games.imageId)
        gameTitle.text = games.gameName

        val marginBottom = 30.dpToPx(context)
        val params = itemView.layoutParams as? ViewGroup.MarginLayoutParams
        params?.setMargins(0, 0, 0, marginBottom)
        itemView.layoutParams = params

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
