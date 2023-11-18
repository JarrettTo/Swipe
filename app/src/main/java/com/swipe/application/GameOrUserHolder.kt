package com.swipe.application

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GameOrUserHolder(itemView: View, private val clickListener: (() -> Unit)? = null) : RecyclerView.ViewHolder(itemView) {
    private val icon: ImageView = itemView.findViewById(R.id.icon)
    private val name: TextView = itemView.findViewById(R.id.name)
    private val deleteButton: Button = itemView.findViewById(R.id.del_button)

    fun bindData(user: Users) {
        icon.setImageResource(user.profile)
        name.text = user.username
    }

    fun bindData(game: Games, isNotDeleteMode: Boolean) {
        icon.setImageResource(game.imageId)
        name.text = game.gameName
        deleteButton.visibility = if (isNotDeleteMode) View.GONE else View.VISIBLE

        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog(name.text.toString().trim())
        }

        clickListener?.let { listener ->
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, GameDetailsActivity::class.java).apply {
                    putExtra("gameDetails", game)
                }
                itemView.context.startActivity(intent)
            }
        }
    }

    private fun showDeleteConfirmationDialog(itemTitle: String) {
        val dialogView = LayoutInflater.from(itemView.context).inflate(R.layout.remove_prompt, null)
        val tvConfirmMessage = dialogView.findViewById<TextView>(R.id.tvDeleteConfirm)
        tvConfirmMessage.text = "Are you sure you want to remove $itemTitle?"

        val dialogBuilder = AlertDialog.Builder(itemView.context)
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
