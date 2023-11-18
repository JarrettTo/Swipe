package com.swipe.application

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SearchAdapter(
    private var gamesList: List<String?>,
    private val itemClickListener: (String) -> Unit
) : RecyclerView.Adapter<SearchAdapter.SearchHolder>() {

    class SearchHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gameTextView: TextView = itemView.findViewById(R.id.list_view_in_search)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_list_view, parent, false)
        return SearchHolder(view)
    }

    override fun onBindViewHolder(holder: SearchHolder, position: Int) {
        val gameName = gamesList[position]
        holder.gameTextView.text = gameName

        // Set click listener
        holder.itemView.setOnClickListener {
            if (gameName != null) {
                itemClickListener.invoke(gameName)
            }
        }
    }

    override fun getItemCount(): Int {
        return gamesList.size
    }

    fun filterList(filteredList: List<String?>) {
        gamesList = filteredList
        notifyDataSetChanged()
    }
}
