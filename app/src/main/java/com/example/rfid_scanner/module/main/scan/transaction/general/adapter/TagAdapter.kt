package com.example.rfid_scanner.module.main.scan.transaction.general.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.rfid_scanner.R
import com.example.rfid_scanner.data.model.Tag
import com.example.rfid_scanner.databinding.ItemTagDetailBinding

class TagAdapter : RecyclerView.Adapter<TagAdapter.TagVH>() {

    private val dataList = mutableListOf<Tag>()

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
        if (dataList.isEmpty()) return "Tidak ada tags yang terdeteksi"
        return null
    }

    inner class TagVH ( private val binding : ItemTagDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Tag) {
            with(binding) {
                tvTagCode.text = data.epc
                tvStockName.text = data.stockName ?: "<no_name>"
                tvTagStatus.text = ("OK")
                binding.root.context?.let {
                    llViewHolder.setBackgroundColor(ContextCompat.getColor(it, R.color.green_item_ok))
                }
            }
        }
    }

    data class TagData(val position: Int, var data: Tag)
}