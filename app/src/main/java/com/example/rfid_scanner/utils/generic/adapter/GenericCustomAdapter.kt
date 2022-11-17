package com.example.rfid_scanner.utils.generic.adapter

import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.rfid_scanner.utils.listener.ItemClickListener
import com.example.rfid_scanner.utils.app.App

class GenericCustomAdapter<T> : RecyclerView.Adapter<BaseViewHolder<T>>() {

    var expressionViewHolderBinding: ((T, ViewBinding, RecyclerView.ViewHolder) -> Unit)? = null
    var expressionOnCreateViewHolder: ((ViewGroup) -> ViewBinding)? = null
    var expressionGetAdapterError: (() -> String?)? = null
    var listener: ItemClickListener? = null

    val dataSet = mutableListOf<T>()
    val mapOfIndex = mutableMapOf<String, Int>()

    val mapOfOperations = mutableMapOf<Int, Function<Unit>>()

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

    fun getError(): String? {
        return expressionGetAdapterError?.let { it() }
    }
}


