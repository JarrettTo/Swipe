package com.swipe.application

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class CustomSpinnerAdapter(context: Context, items: Array<String>) : ArrayAdapter<String>(context, R.layout.custome_spinner_item, items) {
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = inflater.inflate(R.layout.custome_spinner_item, parent, false)
        }

        val textView = view!!.findViewById<TextView>(R.id.spinner_item_text)
        textView.text = getItem(position)

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }
}