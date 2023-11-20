package com.swipe.application

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class GameOrUserAdapter(private val items: MutableList<Any>, private val clickListener: ((Games) -> Unit)? = null, private val listener: PlaylistGameActionListener) : RecyclerView.Adapter<GameOrUserHolder>() {
    var isNotDeleteMode = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameOrUserHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.game_or_user, parent, false)
        return GameOrUserHolder(view, clickListener, listener)
    }

    override fun onBindViewHolder(holder: GameOrUserHolder, position: Int) {
        val item = items[position]
        when (item) {
            is Games -> holder.bindData(item, isNotDeleteMode)
            is Users -> holder.bindData(item)
        }

        // Apply top and bottom margins
        val topBottomMargin = 20.dpToPx(holder.itemView.context)
        val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.topMargin = topBottomMargin
        layoutParams.bottomMargin = topBottomMargin
        holder.itemView.layoutParams = layoutParams
    }

    override fun getItemCount(): Int = items.size

    private fun Int.dpToPx(context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }

    fun addGameToPlaylist(game: Games) {
        items.add(game)
        notifyItemInserted(items.size - 1)
    }

    fun removeGameToPlaylist(game: Games) {
        val index = items.indexOf(game)
        if (index != -1) {
            items.removeAt(index)
            notifyItemRemoved(index)
        }
    }

}
