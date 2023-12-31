package com.swipe.application

import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class GameOrUserHolder(itemView: View, private val clickListener: ((Any) -> Unit)? = null, private val listener: PlaylistGameActionListener?) : RecyclerView.ViewHolder(itemView) {
    private val icon: ImageView = itemView.findViewById(R.id.icon)
    private val name: TextView = itemView.findViewById(R.id.name)
    private val deleteButton: Button = itemView.findViewById(R.id.del_button)

    fun bindData(user: Users) {
        icon.setImageResource(user.profile)
        if(user.profileURL != ""){
            Glide.with(itemView.context)
                .load(user.profileURL)
                .placeholder(R.drawable.dp)
                .error(R.drawable.dp)
                .into(icon)
        } else{
            icon.setImageResource(R.drawable.dp)
        }
        name.text = user.username
    }

    fun bindData(game: Games, isNotDeleteMode: Boolean) {
        if(game.imageId!=0){
            icon.setImageResource(game.imageId)
        } else{
            Glide.with(itemView.context)
                .load(game.imageURL)
                .into(icon);
        }

        name.text = game.gameName
        deleteButton.visibility = if (isNotDeleteMode) View.GONE else View.VISIBLE

        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog(name.text.toString().trim(), game)
        }

        itemView.setOnClickListener {
            clickListener?.invoke(game)
        }
    }

    fun bindData(playlist: Playlist, isNotDeleteMode: Boolean) {
        if(playlist.imageURL != ""){
            Glide.with(itemView.context)
                .load(playlist.imageURL)
                .placeholder(R.drawable.games)
                .error(R.drawable.games)
                .into(icon)
        } else{
            icon.setImageResource(R.drawable.games)
        }

        name.text = playlist.playlistName
        deleteButton.visibility = if (isNotDeleteMode) View.GONE else View.VISIBLE

        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog(name.text.toString().trim(), playlist)
        }

        itemView.setOnClickListener {
            clickListener?.invoke(playlist)
        }
    }

    private fun showDeleteConfirmationDialog(itemTitle: String, game: Games) {
        val dialogView = LayoutInflater.from(itemView.context).inflate(R.layout.remove_prompt, null)
        val tvConfirmMessage = dialogView.findViewById<TextView>(R.id.tvDeleteConfirm)
        tvConfirmMessage.text = "Are you sure you want to remove $itemTitle?"

        val dialogBuilder = AlertDialog.Builder(itemView.context)
            .setView(dialogView)

        val alertDialog = dialogBuilder.create()

        dialogView.findViewById<Button>(R.id.btnConfirmYes).setOnClickListener {
            listener?.onDeletePlaylistGameAction(game)
            alertDialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnConfirmNo).setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun showDeleteConfirmationDialog(itemTitle: String, playlist: Playlist) {
        val dialogView = LayoutInflater.from(itemView.context).inflate(R.layout.remove_prompt, null)
        val tvConfirmMessage = dialogView.findViewById<TextView>(R.id.tvDeleteConfirm)
        tvConfirmMessage.text = "Are you sure you want to remove $itemTitle?"

        val dialogBuilder = AlertDialog.Builder(itemView.context)
            .setView(dialogView)

        val alertDialog = dialogBuilder.create()

        dialogView.findViewById<Button>(R.id.btnConfirmYes).setOnClickListener {
            listener?.onDeletePlaylistAction(playlist)
            alertDialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnConfirmNo).setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
}
