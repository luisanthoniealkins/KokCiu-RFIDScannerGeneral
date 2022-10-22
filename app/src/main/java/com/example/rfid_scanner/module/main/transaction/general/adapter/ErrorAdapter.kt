package com.example.rfid_scanner.module.main.transaction.general.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.rfid_scanner.R
import com.example.rfid_scanner.data.model.Tag
import com.example.rfid_scanner.data.model.Tag.Companion.STATUS_BROKEN
import com.example.rfid_scanner.data.model.Tag.Companion.STATUS_LOST
import com.example.rfid_scanner.data.model.Tag.Companion.STATUS_SOLD
import com.example.rfid_scanner.data.model.Tag.Companion.STATUS_STORED
import com.example.rfid_scanner.data.model.Tag.Companion.STATUS_UNKNOWN
import com.example.rfid_scanner.databinding.ItemTagDetailBinding

class ErrorAdapter() : RecyclerView.Adapter<ErrorAdapter.ErrorTagVH>(){

    private val dataList = mutableListOf<Tag>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ErrorTagVH(
            ItemTagDetailBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ErrorTagVH, position: Int) =
        holder.bind(dataList[position])

    override fun getItemCount() = dataList.size

    fun updateData(isCreate: Boolean, position: Int, data: Tag) {
        if (isCreate) {
            dataList.add(data)
            notifyItemInserted(position)
        } else {
            dataList[position] = data
            notifyItemChanged(position)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearData() {
        dataList.clear()
        notifyDataSetChanged()
    }

    fun getError(): String? {
        for(tag in dataList) {
            when (tag.status) {
                STATUS_UNKNOWN -> return "Beberapa barang tidak dikenal"
                STATUS_STORED -> return "Beberapa barang sudah di gudang"
                STATUS_SOLD -> return "Beberapa barang sudah dijual"
                STATUS_BROKEN -> return "Beberapa barang rusak sudah keluar"
                STATUS_LOST -> return "Beberapa barang rusak sudah disesuaikan (hilang)"
            }
            if (tag.epc.isEmpty()) return "Tag tidak ada kode"
        }
        return null
    }

    inner class ErrorTagVH ( private val binding : ItemTagDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Tag) {
            with(binding) {
                tvTagCode.text = data.epc
                tvStockName.text = data.stockName ?: "<no_name>"

                tvTagStatus.text = when(data.status) {
                    STATUS_UNKNOWN -> "barang tidak\ndikenal"
                    STATUS_SOLD -> "barang sudah\ndijual"
                    STATUS_BROKEN -> "barang rusak\nsudah keluar"
                    STATUS_STORED -> "barang sudah\ndi gudang"
                    STATUS_LOST -> "barang hilang"
                    else -> if (data.epc.isEmpty()) {
                        "tag tidak\nada kode"
                    } else {
                        "-"
                    }
                }
                binding.root.context?.let {
                    llViewHolder.setBackgroundColor(ContextCompat.getColor(it,
                        if (data.status == STATUS_UNKNOWN) R.color.yellow_item_unknown
                        else R.color.red_item_error
                    ))
                }

            }
        }
    }
}