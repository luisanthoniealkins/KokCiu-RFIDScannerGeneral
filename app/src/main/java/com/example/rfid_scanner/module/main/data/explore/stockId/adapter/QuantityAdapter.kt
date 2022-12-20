package com.example.rfid_scanner.module.main.data.explore.stockId.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.rfid_scanner.R
import com.example.rfid_scanner.data.model.Stock
import com.example.rfid_scanner.data.model.StockId
import com.example.rfid_scanner.databinding.ItemButtonQuantityBinding
import com.example.rfid_scanner.utils.listener.ItemClickListener

class QuantityAdapter(
    private val listener: ItemClickListener,
    private val dataList: List<StockId>,
    private val canAddNew: Boolean,
) : RecyclerView.Adapter<QuantityAdapter.QuantityButtonVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        QuantityButtonVH(ItemButtonQuantityBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(VH: QuantityButtonVH, position: Int) =
        if (position < dataList.size) VH.bind(dataList[position])
        else VH.bind(null)

    override fun getItemCount() = dataList.size + if (canAddNew) 1 else 0

    inner class QuantityButtonVH(private val binding: ItemButtonQuantityBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: StockId?) = with(binding){
            if (data != null) {
                btnQty.text = data.unitCount.toString()
                btnQty.setOnClickListener { listener.onItemClick(StockIdSelected(false, data)) }
                btnQty.isEnabled = !canAddNew
            } else {
                btnQty.text = "+"
                btnQty.setOnClickListener { listener.onItemClick(StockIdSelected(true, dataList.first())) }
            }
        }
    }

    data class StockIdSelected(val isAddNew: Boolean, val stockId: StockId)
}