package com.example.rfid_scanner.module.main.scan.transaction.replacement.verify.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.rfid_scanner.R
import com.example.rfid_scanner.data.model.Tag
import com.example.rfid_scanner.databinding.ItemTagDetailBinding
import com.example.rfid_scanner.utils.app.App

class VerifyTagAdapter(private val dataListFull: List<VerifyTagData>) :
    RecyclerView.Adapter<VerifyTagAdapter.VerifyTagVH>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VerifyTagVH(
            ItemTagDetailBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: VerifyTagVH, position: Int) =
        holder.bind(dataListFull[position])

    override fun getItemCount() = dataListFull.size

    fun addData(tags: List<Tag>) {
        tags.map { tag ->
            dataListFull.firstOrNull{ it.data.epc == tag.epc }?.isScanned = true
        }
        refresh()
    }

    fun resetData() {
        dataListFull.map { it.isScanned = false }
        refresh()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refresh() {
        notifyDataSetChanged()
    }

    fun isAllVerified(): Boolean {
        return dataListFull.none { !it.isScanned }
    }

    inner class VerifyTagVH ( private val binding : ItemTagDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: VerifyTagData) {
            with(binding) {
                tvTagCode.text = data.data.epc
                tvStockName.text = data.data.stockName ?: "<no_name>"

                tvTagStatus.text = if (data.isScanned) "OK" else "belum terpindai"
                llViewHolder.setBackgroundColor(ResourcesCompat.getColor(
                    App.res!!,
                    if (data.isScanned) R.color.green_item_ok
                    else R.color.red_item_error,
                    null,
                ))
            }
        }
    }

    data class VerifyTagData(var data: Tag, var isScanned: Boolean)

}