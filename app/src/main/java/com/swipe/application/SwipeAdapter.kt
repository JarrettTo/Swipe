package com.swipe.application

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView

class SwipeAdapter(private val mData:List<Int>): BaseAdapter() {
    override fun getCount(): Int {
        return mData.size
    }

    override fun getItem(p0: Int): Any {
        return mData[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var convertView: View? = p1
        convertView = LayoutInflater.from(p2!!.context).inflate(R.layout.card_items, p2, false)
        val imgViewCard = convertView.findViewById(R.id.imgViewCard) as ImageView
        imgViewCard.setImageResource(mData[p0])
        return convertView
    }

}