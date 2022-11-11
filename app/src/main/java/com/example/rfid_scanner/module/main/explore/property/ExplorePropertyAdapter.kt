package com.example.rfid_scanner.module.main.explore.property

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rfid_scanner.data.model.GeneralProperty
import com.example.rfid_scanner.databinding.ItemExplorePropertyBinding
import com.example.rfid_scanner.utils.generic.Extension.hasPattern
import com.example.rfid_scanner.utils.listener.ItemClickListener
import java.util.*

class ExplorePropertyAdapter(private val listener: ItemClickListener) :
    RecyclerView.Adapter<ExplorePropertyAdapter.ExplorePropertyVH>() {

    private var dataListFull = listOf<GeneralProperty>()
    private var dataList = listOf<GeneralProperty>()
    private var textFilter = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ExplorePropertyVH(ItemExplorePropertyBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(VH: ExplorePropertyVH, position: Int) =
        if (textFilter.isEmpty()) VH.bind(dataListFull[position])
        else VH.bind(dataList[position])

    override fun getItemCount() = if (textFilter.isEmpty()) dataListFull.size else dataList.size

    fun setProperty(stocks: List<GeneralProperty>) {
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

        val filteredList = mutableListOf<GeneralProperty>()
        dataListFull.map { data ->
            val str = "${data.propertyCode} ${data.propertyName}"
            if (str.hasPattern(textFilter)) filteredList.add(data)
        }
        dataList = filteredList
        notifyDataSetChanged()
    }

    inner class ExplorePropertyVH(private val binding: ItemExplorePropertyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: GeneralProperty) = with(binding){
            tvCode.text = data.propertyCode
            tvName.text = data.propertyName
            root.setOnClickListener { listener.onItemClick(data) }
        }
    }

}


