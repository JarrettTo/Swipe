package com.swipe.application

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class GroupHolder(itemView: View, private val context: Context, private val listener: GroupDetailsListener, private val lifecycleScope: LifecycleCoroutineScope) : RecyclerView.ViewHolder(itemView) {
    private val banner: ImageView = itemView.findViewById(R.id.grpBanner)
    private val count: TextView = itemView.findViewById(R.id.grpCount)
    private val name: TextView = itemView.findViewById(R.id.grpName)
    private val desc: TextView = itemView.findViewById(R.id.grpDesc)
    private val button: Button = itemView.findViewById(R.id.more)
    private val container: CardView = itemView.findViewById(R.id.group_container)
    private var isCreator = false

    fun bindData(group: Groups) {
        count.text = group.count.toString()
        name.text = group.name
        desc.text = group.desc

        if (group.image != "") {
            Glide.with(itemView.context)
                .load(group.image)
                .into(banner)
        } else {
            banner.setImageResource(R.drawable.groups)
        }

        button.setOnClickListener { anchorView ->
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val popupView = inflater.inflate(R.layout.dropdown, null)
            val del: LinearLayout = popupView.findViewById(R.id.dropdown_container)
            var text: TextView = popupView.findViewById(R.id.delete_text)
            var img: ImageView = popupView.findViewById(R.id.removeGroup)
            val username = UserSession(context).userName

            if (group.creator != username){
                text.text = "Leave"
                img.setImageResource(R.drawable.exit)
                isCreator = false
            } else {
                text.text = "Remove"
                img.setImageResource(R.drawable.delete)
                isCreator = true
            }

            val popupWindow = PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true)

            del.setOnClickListener {
                if (username != null) {
                    showDeleteConfirmationDialog(group, username, isCreator)
                }
                popupWindow.dismiss()
            }

            popupWindow.showAsDropDown(anchorView)
        }

        container.setOnClickListener {
            listener.onGroupClicked(group)
        }
    }

    private fun showDeleteConfirmationDialog(group: Groups, username: String, creator: Boolean) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.remove_prompt, null)
        val tvConfirmMessage = dialogView.findViewById<TextView>(R.id.tvDeleteConfirm)
        if (creator) {
            tvConfirmMessage.text = "Are you sure you want to remove ${group.name.toString()}?"
        } else {
            tvConfirmMessage.text = "Are you sure you want to leave ${group.name.toString()}?"
        }

        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)

        val alertDialog = dialogBuilder.create()

        dialogView.findViewById<Button>(R.id.btnConfirmYes).setOnClickListener {
            if (creator) {
                lifecycleScope.launch {
                    GroupDataHelper().removeGroup(group.id)
                    UserSession(context).removeGroupId(group.id)
                    listener.onGroupDeleted(group)
                    alertDialog.dismiss()
                }
            } else {
                lifecycleScope.launch {
                    GroupDataHelper().leaveGroup(group.id, username)
                    UserSession(context).removeGroupId(group.id)
                    listener.onGroupDeleted(group)
                    alertDialog.dismiss()
                }
            }
        }

        dialogView.findViewById<Button>(R.id.btnConfirmNo).setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
}
