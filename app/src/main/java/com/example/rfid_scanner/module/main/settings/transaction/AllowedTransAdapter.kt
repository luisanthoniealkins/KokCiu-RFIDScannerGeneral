package com.example.rfid_scanner.module.main.settings.transaction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rfid_scanner.databinding.ItemTransSettingsBinding

class AllowedTransAdapter : RecyclerView.Adapter<AllowedTransAdapter.QuantityButtonVH>() {

    private var dataList: List<AllowedTransData> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        QuantityButtonVH(ItemTransSettingsBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(VH: QuantityButtonVH, position: Int) =
        VH.bind(dataList[position])

    override fun getItemCount() = dataList.size

    fun setData(data : List<AllowedTransData>) {
        dataList = data
    }

    fun getData() = dataList

    inner class QuantityButtonVH(private val binding: ItemTransSettingsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: AllowedTransData) = with(binding){
            cbText.text = ("${data.from} -> ${data.to}")
            cbText.isChecked = data.isChecked
            cbText.setOnCheckedChangeListener { _, b ->
                dataList[absoluteAdapterPosition].isChecked = b
            }
        }
    }

    data class AllowedTransData(val from: String, val to: String, var isChecked: Boolean)

}