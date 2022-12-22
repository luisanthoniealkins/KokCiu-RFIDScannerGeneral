package com.example.rfid_scanner.module.main.data.info.transaction_rfid

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rfid_scanner.databinding.ItemTagScannerBinding
import com.example.rfid_scanner.utils.extension.StringExt.hasPattern
import java.util.*

class TransactionRFIDAdapter :
    RecyclerView.Adapter<TransactionRFIDAdapter.TransactionRFIDVH>() {

    private var dataListFull = listOf<String>()
    private var dataList = listOf<String>()
    private var textFilter = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TransactionRFIDVH(ItemTagScannerBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(VH: TransactionRFIDVH, position: Int) =
        if (textFilter.isEmpty()) VH.bind(dataListFull[position])
        else VH.bind(dataList[position])

    override fun getItemCount() = if (textFilter.isEmpty()) dataListFull.size else dataList.size

    fun setRFID(rfids: List<String>) {
        dataListFull = rfids
        filter(textFilter)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filter(newTextFilter: String) {
        textFilter = newTextFilter.lowercase(Locale.getDefault()).trim { it <= ' ' }
        if (textFilter.isEmpty()) {
            notifyDataSetChanged()
            return
        }

        val filteredList = mutableListOf<String>()
        dataListFull.map { data ->
            if (data.hasPattern(textFilter)) filteredList.add(data)
        }
        dataList = filteredList
        notifyDataSetChanged()
    }

    inner class TransactionRFIDVH(private val binding: ItemTagScannerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: String) = with(binding){
            tvName.text = data
        }
    }

}


