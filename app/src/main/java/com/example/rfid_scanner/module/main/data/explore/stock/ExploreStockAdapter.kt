package com.example.rfid_scanner.module.main.data.explore.stock

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rfid_scanner.data.model.Stock
import com.example.rfid_scanner.databinding.ItemExploreStockBinding
import com.example.rfid_scanner.utils.extension.StringExt.hasPattern
import com.example.rfid_scanner.utils.helper.TextHelper.emptyString
import com.example.rfid_scanner.utils.listener.ItemClickListener
import java.util.*

class ExploreStockAdapter(
    private val listener: ItemClickListener,
) : RecyclerView.Adapter<ExploreStockAdapter.ExploreStockVH>() {

    private var dataListFull = listOf<Stock>()
    private var dataList = listOf<Stock>()
    private var textFilter = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ExploreStockVH(ItemExploreStockBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(VH: ExploreStockVH, position: Int) =
        if (textFilter.isEmpty()) VH.bind(dataListFull[position])
        else VH.bind(dataList[position])

    override fun getItemCount() = if (textFilter.isEmpty()) dataListFull.size else dataList.size

    fun setStocks(stocks: List<Stock>) {
        dataListFull = stocks
        filter(textFilter)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filter(newTextFilter: String) {
        textFilter = newTextFilter.lowercase(Locale.getDefault()).trim { it <= ' ' }
        if (textFilter.isEmpty()) {
            notifyDataSetChanged()
            return
        }

        val filteredList = mutableListOf<Stock>()
        dataListFull.map { stock ->
            val str = "${stock.name} ${stock.code} ${stock.vehicleType}"
            if (str.hasPattern(textFilter)) filteredList.add(stock)
        }
        dataList = filteredList
        notifyDataSetChanged()
    }

    inner class ExploreStockVH(private val binding: ItemExploreStockBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Stock) = with(binding){
            tvName.text = data.name ?: emptyString()
            tvCode.text = data.code
            tvType.text = data.vehicleType
            tvAvailableStock.text = "${data.availableStock} ${data.unit}"
            root.setOnClickListener { listener.onItemClick(data) }
        }
    }
}


