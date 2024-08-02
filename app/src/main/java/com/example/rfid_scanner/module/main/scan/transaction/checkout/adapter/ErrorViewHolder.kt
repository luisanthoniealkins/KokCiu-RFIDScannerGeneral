package com.example.rfid_scanner.module.main.scan.transaction.checkout.adapter

import android.view.LayoutInflater
import com.example.rfid_scanner.R
import com.example.rfid_scanner.data.model.Tag
import com.example.rfid_scanner.data.model.Tag.Companion.isProperTag
import com.example.rfid_scanner.databinding.ItemTagDetailBinding
import com.example.rfid_scanner.utils.generic.adapter.GenericCustomAdapter
import com.example.rfid_scanner.utils.helper.ColorHelper.gColor

class ErrorViewHolder {

    companion object {
        const val addErrorTag = 0
        const val clearErrorTags = 1
    }

    fun getAdapter(): GenericCustomAdapter<Tag> {
        val adapter = GenericCustomAdapter<Tag>()
        with(adapter) {
            expressionOnCreateViewHolder = {
                ItemTagDetailBinding.inflate(LayoutInflater.from(it.context), it, false)
            }

            expressionViewHolderBinding = { item, viewBinding, _ ->
                val view = viewBinding as ItemTagDetailBinding
                with(view) {
                    tvTagCode.text = item.epc
                    tvStockName.text = item.stockName ?: "<no_name>"

                    tvTagStatus.text = when {
                        !item.epc.isProperTag() -> "format tag\ntidak sesuai"
                        item.status == Tag.STATUS_UNKNOWN -> "barang tidak\ndikenal"
                        item.status == Tag.STATUS_SOLD -> "barang sudah\ndijual"
                        item.status == Tag.STATUS_BROKEN -> "barang rusak\nsudah keluar"
                        item.status == Tag.STATUS_STORED -> "barang tidak\nsesuai bon"
                        item.status == Tag.STATUS_LOST -> "barang hilang"
                        else -> "-"
                    }

                    llViewHolder.setBackgroundColor(
                        gColor(
                            when {
                                !item.epc.isProperTag() -> R.color.yellow_item_unknown
                                item.status == Tag.STATUS_UNKNOWN -> R.color.yellow_item_unknown
                                item.status == Tag.STATUS_SOLD -> R.color.red_item_error
                                item.status == Tag.STATUS_BROKEN -> R.color.red_item_error
                                item.status == Tag.STATUS_STORED -> R.color.red_item_error
                                item.status == Tag.STATUS_LOST -> R.color.red_item_error
                                else -> R.color.yellow_item_unknown
                            }
                        )
                    )

                    /**
                     * TODO:
                     * item on click set to scan window
                     */
//                itemView.setOnClickListener(View.OnClickListener { v: View? ->
//                    (mContext as MainActivity).initFragmentView(
//                        CheckTagFragment(tag.getEPC())
//                    )
//                })
                }
            }

            expressionGetAdapterError = {
                var error: String? = null
                for (tag in dataSet) {
                    when{
                        !tag.epc.isProperTag() -> error = "Format tag tidak sesuai"
                        tag.status == Tag.STATUS_STORED -> error = "Beberapa barang tidak sesuai bon"
                        tag.status == Tag.STATUS_SOLD -> error = "Beberapa barang sudah dijual"
                        tag.status == Tag.STATUS_BROKEN -> error = "Beberapa barang rusak sudah keluar"
                        tag.status == Tag.STATUS_LOST -> error = "Beberapa barang rusak sudah disesuaikan (hilang)"
                    }
                    if (error != null) break
                }
                error
            }

            expressionGetUnknownTags = {
                dataSet.filter { it.epc.isProperTag() && it.status == Tag.STATUS_UNKNOWN }.map { it.epc }
            }

            /**
             * Custom operations start heere
             */
            mapOfOperations[addErrorTag] = fun(tag: Tag) {
                mapOfIndex[tag.epc]
                    ?.let {
                        dataSet[it] = tag
                        notifyItemChanged(it)
                    }
                    ?: run {
                        dataSet.add(tag)
                        mapOfIndex[tag.epc] = dataSet.lastIndex
                        notifyItemInserted(dataSet.lastIndex)
                    }
            }

            mapOfOperations[clearErrorTags] = fun() {
                mapOfIndex.clear()
                while (dataSet.isNotEmpty()) {
                    dataSet.removeLast()
                    notifyItemRemoved(dataSet.size)
                }
            }
        }
        return adapter
    }

}