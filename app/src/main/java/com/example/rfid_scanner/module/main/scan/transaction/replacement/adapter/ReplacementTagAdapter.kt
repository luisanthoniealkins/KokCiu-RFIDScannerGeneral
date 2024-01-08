package com.example.rfid_scanner.module.main.scan.transaction.replacement.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.rfid_scanner.R
import com.example.rfid_scanner.data.model.Tag
import com.example.rfid_scanner.databinding.ItemTagDetailBinding
import com.example.rfid_scanner.utils.app.App

class ReplacementTagAdapter :
    RecyclerView.Adapter<ReplacementTagAdapter.TagVH>()  {

    private val dataList = mutableListOf<Tag>()
    private var pivotTag : Tag? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TagVH(
            ItemTagDetailBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: TagVH, position: Int) =
        holder.bind(dataList[position])

    override fun getItemCount() = dataList.size

    fun setPivotTag(tag: Tag){
        pivotTag = tag
    }

    fun updateData(isCreate: Boolean, position: Int, data: Tag) {
        if (isCreate) {
            dataList.add(data)
            notifyItemInserted(position)
        } else {
            dataList[position] = data
            notifyItemChanged(position)
        }
    }

    fun getScannedTags() : List<Tag>{
        return dataList
    }


    @SuppressLint("NotifyDataSetChanged")
    fun clearData() {
        dataList.clear()
        notifyDataSetChanged()
    }

    fun getErrorMessage() = when {
            dataList.isEmpty()->"Tidak ada tags yang terdeteksi"
            dataList.size > 1 -> "Ganti EPC hanya diperbolehkan jika jumlah tag adalah 1"
            dataList[0].epc == pivotTag?.epc -> "Ganti EPC tidak boleh dilakukan terhadap kode yang sama"
            dataList[0].status != Tag.STATUS_UNKNOWN -> "Ganti EPC hanya boleh dilakukan ke tag yang tidak dikenal"
            else -> null
    }


    inner class TagVH ( private val binding : ItemTagDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Tag) {
            with(binding) {
                tvTagCode.text = data.epc
                tvStockName.text = data.stockName ?: "<no_name>"

                tvTagStatus.text = when {
                    data.epc == pivotTag?.epc -> "kode sama\nditemukan"
                    data.status == Tag.STATUS_SOLD -> "barang sudah\ndijual"
                    data.status == Tag.STATUS_BROKEN -> "barang rusak\nsudah keluar"
                    data.status == Tag.STATUS_STORED -> "barang masih\ndi gudang"
                    data.status == Tag.STATUS_LOST -> "barang hilang"
                    else -> "OK"
                }

                binding.root.context?.let {
                    llViewHolder.setBackgroundColor(
                        ContextCompat.getColor(it,
                            when {
                                data.epc == pivotTag?.epc -> R.color.red_item_error
                                data.status != Tag.STATUS_UNKNOWN -> R.color.red_item_error
                                else -> R.color.green_item_ok
                            }))
                }
            }
        }
    }
}