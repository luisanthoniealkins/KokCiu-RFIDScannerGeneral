package com.example.rfid_scanner.module.template.viewholder

import android.view.LayoutInflater
import com.example.rfid_scanner.databinding.ItemTemplateViewHolderBinding
import com.example.rfid_scanner.utils.generic.adapter.GenericAdapter

class TemplateViewHolder(private val dataSet: MutableList<Int>) {

    fun getAdapter(): GenericAdapter<Int> {
        val adapter = GenericAdapter(dataSet)
        adapter.expressionOnCreateViewHolder = {
            ItemTemplateViewHolderBinding.inflate(LayoutInflater.from(it.context), it, false)
        }
        adapter.expressionViewHolderBinding = { item, viewBinding, _ ->
            val view = viewBinding as ItemTemplateViewHolderBinding
            with(view) {
                tvTest.text = item.toString()
            }
        }
        return adapter
    }

}