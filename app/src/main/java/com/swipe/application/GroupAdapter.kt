package com.swipe.application

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView

class GroupAdapter(private var data: ArrayList<Groups>?, private val listener: GroupDetailsListener, private val lifecycleScope: LifecycleCoroutineScope) : RecyclerView.Adapter<GroupHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.group, parent, false)
        return GroupHolder(view, parent.context, listener, lifecycleScope)
    }


    override fun onBindViewHolder(holder: GroupHolder, position: Int) {

        holder.bindData(data!![position])

    }

    override fun getItemCount(): Int {
        return data!!.size


    }
    fun updateGroups(newGroups: ArrayList<Groups>) {
        this.data?.clear()
        this.data?.addAll(newGroups)
        notifyDataSetChanged()
    }

    fun addGroup(newGroup: Groups) {
        data?.add(newGroup)
        notifyDataSetChanged()
    }

    fun removeGroup(groupToRemove: Groups) {
        val iterator = data?.iterator()
        while (iterator?.hasNext() == true) {
            if (iterator.next().id == groupToRemove.id) {
                iterator.remove()
                notifyDataSetChanged()
                break
            }
        }
    }

    fun updateGroup(updatedGroup: Groups) {
        val iterator = data?.iterator()
        while (iterator?.hasNext() == true) {
            val currentItem = iterator.next()
            if (currentItem.id == updatedGroup.id) {
                val index = data?.indexOf(currentItem)
                if (index != null && index != -1) {
                    data?.set(index, updatedGroup)
                    notifyItemChanged(index)
                }
                break
            }
        }
    }
}