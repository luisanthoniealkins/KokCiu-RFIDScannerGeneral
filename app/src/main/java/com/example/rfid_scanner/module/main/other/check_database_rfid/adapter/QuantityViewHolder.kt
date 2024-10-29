package com.example.rfid_scanner.module.main.other.check_database_rfid.adapter

import android.view.LayoutInflater
import com.example.rfid_scanner.data.model.StockId
import com.example.rfid_scanner.data.model.StockRFID
import com.example.rfid_scanner.data.model.Transaction
import com.example.rfid_scanner.databinding.ItemButtonQuantityBinding
import com.example.rfid_scanner.databinding.ItemStockRfidBinding
import com.example.rfid_scanner.databinding.ItemTransactionDetailBinding
import com.example.rfid_scanner.utils.generic.adapter.GenericAdapter
import com.example.rfid_scanner.utils.listener.ItemClickListener

class QuantityViewHolder(private val data: StockRFID, private val listener: ItemClickListener? = null) {

    fun getAdapter(): GenericAdapter<Int> {
        val adapter = GenericAdapter(data.unitCounts)
        adapter.listener = listener
        adapter.expressionOnCreateViewHolder = {
            ItemButtonQuantityBinding.inflate(LayoutInflater.from(it.context), it, false)
        }
        adapter.expressionViewHolderBinding = { item, viewBinding, _ ->
            val view = viewBinding as ItemButtonQuantityBinding
            with(view) {
                btnQty.text = item.toString()
                btnQty.setOnClickListener { adapter.listener?.onItemClick(Triple(data.stockCode,data.stockName,item)) }
            }
        }
        return adapter
    }

}