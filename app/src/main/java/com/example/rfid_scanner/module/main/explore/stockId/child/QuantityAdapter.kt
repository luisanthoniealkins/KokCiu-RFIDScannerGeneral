package com.example.rfid_scanner.module.main.explore.stockId.child

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rfid_scanner.data.model.StockId
import com.example.rfid_scanner.databinding.ItemButtonQuantityBinding
import com.example.rfid_scanner.utils.generic.ItemClickListener

class QuantityAdapter(
    private val listener: ItemClickListener,
    private val dataList: List<StockId>,
) : RecyclerView.Adapter<QuantityAdapter.QuantityButtonVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        QuantityButtonVH(ItemButtonQuantityBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(VH: QuantityButtonVH, position: Int) =
        VH.bind(dataList[position])

    override fun getItemCount() = dataList.size

    inner class QuantityButtonVH(private val binding: ItemButtonQuantityBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: StockId) = with(binding){
            btnQty.text = data.unitCount.toString()
            btnQty.setOnClickListener { listener.onItemClick(data) }
        }
    }
}