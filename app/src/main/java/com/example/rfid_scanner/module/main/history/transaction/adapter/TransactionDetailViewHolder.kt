package com.example.rfid_scanner.module.main.history.transaction.adapter

import android.view.LayoutInflater
import com.example.rfid_scanner.data.model.Transaction
import com.example.rfid_scanner.databinding.ItemTransactionDetailBinding
import com.example.rfid_scanner.utils.generic.adapter.GenericAdapter

class TransactionDetailViewHolder(private val dataSet: MutableList<Transaction.TransactionDetail>) {

    fun getAdapter(): GenericAdapter<Transaction.TransactionDetail> {
        val adapter = GenericAdapter(dataSet)
        adapter.expressionOnCreateViewHolder = {
            ItemTransactionDetailBinding.inflate(LayoutInflater.from(it.context), it, false)
        }
        adapter.expressionViewHolderBinding = { item, viewBinding, _ ->
            val view = viewBinding as ItemTransactionDetailBinding
            with(view) {
                val qty = "${item.stockQuantity} ${item.stockUnit}"
                tvCode.text = item.stockCode
                tvName.text = item.stockName
                tvVehicleType.text = item.stockVehicleType
                tvQuantity.text = qty
            }
        }
        return adapter
    }

}