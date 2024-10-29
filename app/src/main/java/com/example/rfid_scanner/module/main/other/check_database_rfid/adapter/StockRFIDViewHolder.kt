package com.example.rfid_scanner.module.main.other.check_database_rfid.adapter

import android.view.LayoutInflater
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rfid_scanner.data.model.StockRFID
import com.example.rfid_scanner.databinding.ItemStockRfidBinding
import com.example.rfid_scanner.utils.generic.adapter.GenericAdapter
import com.example.rfid_scanner.utils.listener.ItemClickListener

class StockRFIDViewHolder(private val dataSet: MutableList<StockRFID>) {

    fun getAdapter(): GenericAdapter<StockRFID> {
        val adapter = GenericAdapter(dataSet)
        adapter.expressionOnCreateViewHolder = {
            ItemStockRfidBinding.inflate(LayoutInflater.from(it.context), it, false)
        }
        adapter.expressionViewHolderBinding = { item, viewBinding, _ ->
            val view = viewBinding as ItemStockRfidBinding
            with(view) {
                tvStockName.text = item.stockName
                tvStockCode.text = item.stockCode
                tvStockQuantity.text = item.stockQuantity.toString()
                tvRfidQuantity.text = item.rfidQuantity.toString()

                rvItem.layoutManager = GridLayoutManager(root.context, 4)
                rvItem.adapter = QuantityViewHolder(item, adapter.listener).getAdapter()
            }
        }
        return adapter
    }

}