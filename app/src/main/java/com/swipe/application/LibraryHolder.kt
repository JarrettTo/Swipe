package com.swipe.application

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
    private val playlistLogo: ImageView = itemView.findViewById(R.id.icon)
    private val playlistName: TextView = itemView.findViewById(R.id.playlist_name)
    private val user: TextView = itemView.findViewById(R.id.user)
    private val deleteButton: Button = itemView.findViewById(R.id.del_button)

    fun bindData(playlist: Playlist, isNotDeleteMode: Boolean) {
        playlist.imageId?.let { playlistLogo.setImageResource(it) }
        playlistName.text = playlist.playlistName
        user.text = playlist.username

        if (playlistName.text.toString().trim() != "Liked Games"){
            deleteButton.visibility = if (isNotDeleteMode) View.GONE else View.VISIBLE
        }
        
        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog(playlistName.text.toString().trim())
        }

        val marginTop = 20.dpToPx(context)
        val marginBottom = 10.dpToPx(context)
        val marginRight = 10.dpToPx(context)
        val marginLeft = 10.dpToPx(context)
        val params = itemView.layoutParams as? ViewGroup.MarginLayoutParams
        params?.setMargins(marginLeft, marginTop, marginRight, marginBottom)
        itemView.layoutParams = params

        clickListener?.let { listener ->
            itemView.setOnClickListener {
                val intent = Intent(context, PlaylistDetailsActivity::class.java)
                val playlistDetailsBundle = Bundle().apply {
                    putSerializable("playlistDetails", playlist)
                }
                intent.putExtra("playlistDetails", playlistDetailsBundle)

                context.startActivity(intent)
                listener.invoke()
            }
        }
    }

    private fun showDeleteConfirmationDialog(itemTitle: String) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.remove_prompt, null)
        val tvConfirmMessage = dialogView.findViewById<TextView>(R.id.tvDeleteConfirm)
        tvConfirmMessage.text = "Are you sure you want to remove $itemTitle?"

        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)

        val alertDialog = dialogBuilder.create()

        dialogView.findViewById<Button>(R.id.btnConfirmYes).setOnClickListener {
            // Handle "Yes" action
            alertDialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnConfirmNo).setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

}
