package com.example.rfid_scanner.module.main.explore.stockId

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rfid_scanner.data.model.Stock
import com.example.rfid_scanner.data.model.StockId
import com.example.rfid_scanner.databinding.ItemExploreStockBinding
import com.example.rfid_scanner.module.main.explore.stockId.child.QuantityAdapter
import com.example.rfid_scanner.utils.generic.Extension.hasPattern
import com.example.rfid_scanner.utils.listener.ItemClickListener
import com.example.rfid_scanner.utils.helper.TextHelper.emptyString
import java.util.*

class ExploreStockIdAdapter(private val listener: ItemClickListener) :
    RecyclerView.Adapter<ExploreStockIdAdapter.ExploreStockIdVH>() {

    private var dataListFull = listOf<StockIdData>()
    private var dataList = listOf<StockIdData>()
    private var textFilter = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ExploreStockIdVH(ItemExploreStockBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(VH: ExploreStockIdVH, position: Int) =
        if (textFilter.isEmpty()) VH.bind(dataListFull[position])
        else VH.bind(dataList[position])

    override fun getItemCount() = if (textFilter.isEmpty()) dataListFull.size else dataList.size


    fun setStockIds(stocks: List<StockIdData>) {
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

        val filteredList = mutableListOf<StockIdData>()
        dataListFull.map { data ->
            val str = "${data.stock.name} ${data.stock.code} ${data.stock.vehicleType}"
            if (str.hasPattern(textFilter)) filteredList.add(data)
        }
        dataList = filteredList
        notifyDataSetChanged()
    }

    inner class ExploreStockIdVH(private val binding: ItemExploreStockBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: StockIdData) = with(binding){
            tvName.text = data.stock.name ?: emptyString()
            tvCode.text = data.stock.code
            tvType.text = data.stock.vehicleType
            tvAvailableStock.text = ("${data.stock.availableStock} ${data.stock.unit}")
            rvItem.layoutManager = GridLayoutManager(root.context, 4)
            rvItem.adapter = QuantityAdapter(listener, data.stockIds.sortedBy { it.unitCount })
        }
    }

    data class StockIdData(val stock: Stock, val stockIds: List<StockId>)
}


