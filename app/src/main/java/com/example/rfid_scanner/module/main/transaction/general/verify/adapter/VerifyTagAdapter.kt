package com.example.rfid_scanner.module.main.transaction.general.verify.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.rfid_scanner.R
import com.example.rfid_scanner.data.model.Tag
import com.example.rfid_scanner.databinding.ItemTagDetailBinding

class VerifyTagAdapter(private val context: Context?, private val dataListFull : List<VerifyTagData>) :
    RecyclerView.Adapter<VerifyTagAdapter.VerifyTagVH>()  {

    private var dataList = List(dataListFull.size) { dataListFull[it] }
    private var isShowingOK = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VerifyTagVH(
            ItemTagDetailBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: VerifyTagVH, position: Int) =
        if (isShowingOK) holder.bind(dataListFull[position])
        else holder.bind(dataList[position])

    override fun getItemCount() = if (isShowingOK) dataListFull.size else dataList.size

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

    fun setShowingOK(isShowingOK: Boolean) {
        this.isShowingOK = isShowingOK
        refresh()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refresh() {
        if (!isShowingOK) {
            dataList = dataListFull.filter { !it.isScanned }
        }
        notifyDataSetChanged()
    }

    fun isAllVerified(): Boolean {
        var verified = true
        dataListFull.map { verified = verified and it.isScanned }
        return verified
    }

    inner class VerifyTagVH ( private val binding : ItemTagDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: VerifyTagData) {
            with(binding) {
                tvTagCode.text = data.data.epc
                tvStockName.text = data.data.stockName ?: "<no_name>"

                tvTagStatus.text = if (data.isScanned) "OK" else "belum terpindai"
                context?.let {
                    viewIndicator.setBackgroundColor(
                        ContextCompat.getColor(
                            it,
                            if (data.isScanned) R.color.green_item_ok
                            else R.color.red_item_error
                        )
                    )
                }
            }
        }
    }

    data class VerifyTagData(var data: Tag, var isScanned: Boolean)

}