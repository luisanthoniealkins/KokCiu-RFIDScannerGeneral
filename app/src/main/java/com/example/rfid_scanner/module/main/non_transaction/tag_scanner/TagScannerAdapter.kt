package com.example.rfid_scanner.module.main.non_transaction.tag_scanner

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rfid_scanner.data.model.TagEPCQty
import com.example.rfid_scanner.databinding.ItemTagScannerBinding

class TagScannerAdapter : RecyclerView.Adapter<TagScannerAdapter.TagScannerVH>() {

    private val dataList = mutableListOf<TagEPCQty>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TagScannerVH(ItemTagScannerBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: TagScannerVH, position: Int) =
        holder.bind(dataList[position])

    override fun getItemCount(): Int = dataList.size

    fun updateData(isCreate: Boolean, position: Int, data: TagEPCQty) {
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

    class TagScannerVH (private val binding : ItemTagScannerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: TagEPCQty) {
            with(binding) {
                tvName.text = data.epc
                tvQuantity.text = ("${data.quantity}x")
            }
        }
    }

    data class TagScannerData(val position: Int, var data: TagEPCQty)
}