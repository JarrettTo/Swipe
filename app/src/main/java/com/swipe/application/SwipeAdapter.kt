package com.swipe.application

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button

class SwipeAdapter(private val mData: List<Games>, private var onItemClick: (Games) -> Unit): BaseAdapter() {
    override fun getCount(): Int {
        return mData.size
    }

    override fun getItem(p0: Int): Any {
        return mData[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var holder: SwipeHolder? = convertView?.tag as? SwipeHolder
        val convertView = LayoutInflater.from(parent?.context).inflate(R.layout.card_items, parent, false)
        if (holder == null) {
            holder = SwipeHolder(convertView)
            convertView.tag = holder
        }

        val currentItem = mData[position]

        holder.bindData(currentItem)

        val seeMoreButton = convertView.findViewById<Button>(R.id.seeMoreButton)
        val seeMoreGenresButton = convertView.findViewById<Button>(R.id.seeMoreGenresButton)

        seeMoreButton?.setOnClickListener {
            onItemClick(currentItem)
        }

        seeMoreGenresButton?.setOnClickListener {
            onItemClick(currentItem)
        }

        return convertView
    }

}