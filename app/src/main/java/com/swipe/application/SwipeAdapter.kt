package com.swipe.application

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button

class SwipeAdapter(private val mData: ArrayList<Games>, private var onItemClick: (Games) -> Unit): BaseAdapter() {
    var swipeCount = 0
    var onDataChanged: (() -> Unit)? = null
    override fun getCount(): Int {
        return mData.size
    }

    override fun getItem(p0: Int): Any {
        return mData[p0]
    }
    fun removeItem(p0: Int) {
        mData.removeAt(p0)
    }
    fun updateList() {
       //remove item at the top of the stack
        swipeCount+=1
        if(swipeCount > 7){
            Log.d("MDATA", "Current size:${mData.size}")
            mData.addAll(DataHelper.retrieveGames(15))

            val gameNames: List<String?> = mData.map { it.gameName }
            Log.d("MDATA", "New MDATA Content:${gameNames}")
            swipeCount=0
            notifyDataSetChanged()
        }


    }


    override fun notifyDataSetChanged() {
        Log.d("MDATA", "Updated size:${mData.size}")
        super.notifyDataSetChanged()

        onDataChanged?.invoke()
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

        seeMoreButton?.setOnClickListener {
            onItemClick(currentItem)
        }

        return convertView
    }


}