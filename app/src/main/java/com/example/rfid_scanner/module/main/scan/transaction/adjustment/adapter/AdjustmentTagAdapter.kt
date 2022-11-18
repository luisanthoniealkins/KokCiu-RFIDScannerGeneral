package com.example.rfid_scanner.module.main.scan.transaction.adjustment.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.rfid_scanner.R
import com.example.rfid_scanner.data.model.Tag
import com.example.rfid_scanner.databinding.ItemTagDetailBinding
import com.example.rfid_scanner.utils.app.App

class AdjustmentTagAdapter :
    RecyclerView.Adapter<AdjustmentTagAdapter.VerifyTagVH>()  {

    private val dataListFull= mutableListOf<AdjustmentTagData>()
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

    fun addNewData(tags: List<Tag>) {
        dataListFull.addAll(tags.map { AdjustmentTagData(it, false) })
        refresh()
    }

    fun addData(tag: Tag) {
        dataListFull.firstOrNull{ it.data.epc == tag.epc }?.isScanned = true
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

    fun getScannedTags() = dataListFull.filter { it.isScanned }.map { it.data }
    fun getUnScannedTags() = dataListFull.filter { !it.isScanned }.map { it.data }

    fun clearData() {
        dataListFull.clear()
        refresh()
    }

    inner class VerifyTagVH ( private val binding : ItemTagDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: AdjustmentTagData) {
            with(binding) {
                tvTagCode.text = data.data.epc
                tvStockName.text = data.data.stockName ?: "<no_name>"

                tvTagStatus.text = if (data.isScanned) "OK" else "belum terpindai"
                llViewHolder.setBackgroundColor(ResourcesCompat.getColor(
                    App.res!!,
                    if (data.isScanned) R.color.green_item_ok
                    else R.color.light_gray_item_default,
                    null,
                ))

                /**
                 * TAMBAHIN CHECKER
                 */

//                itemView.setOnClickListener { v: View? ->
//                    (mContext as MainActivity).initFragmentView(
//                        CheckTagFragment(tag.getEPC())
//                    )
//                }
            }
        }
    }

    data class AdjustmentTagData(var data: Tag, var isScanned: Boolean)

}