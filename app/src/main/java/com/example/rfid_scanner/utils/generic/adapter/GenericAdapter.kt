package com.example.rfid_scanner.utils.generic.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.rfid_scanner.utils.listener.ItemClickListener

class GenericAdapter<T>(private val dataSet: MutableList<T>) : RecyclerView.Adapter<BaseViewHolder<T>>() {

    var expressionViewHolderBinding: ((T, ViewBinding, RecyclerView.ViewHolder) -> Unit)? = null
    var expressionOnCreateViewHolder: ((ViewGroup) -> ViewBinding)? = null
    var listener: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T> {
        return expressionOnCreateViewHolder?.let {
            it(parent)
        }?.let { BaseViewHolder(it, expressionViewHolderBinding!!) }!!
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun addData(data: T){
        dataSet.add(data)
        notifyItemInserted(dataSet.lastIndex)
    }

    fun updateData(pos: Int, data: T) {
        if (pos > dataSet.lastIndex) addData(data)
        else {
            dataSet[pos] = data
            notifyItemChanged(pos)
        }
    }

    fun removeData(pos: Int) {
        dataSet.removeAt(pos)
        notifyItemRemoved(pos)
    }

    fun getData(pos: Int) = dataSet[pos]

    @SuppressLint("NotifyDataSetChanged")
    fun clearData() {
        dataSet.clear()
        notifyDataSetChanged()
    }

}


