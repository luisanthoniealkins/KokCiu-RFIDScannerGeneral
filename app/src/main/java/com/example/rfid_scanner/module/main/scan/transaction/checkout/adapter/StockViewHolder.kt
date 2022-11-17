package com.example.rfid_scanner.module.main.scan.transaction.checkout.adapter

import android.view.LayoutInflater
import com.example.rfid_scanner.R
import com.example.rfid_scanner.data.model.Stock
import com.example.rfid_scanner.data.model.StockRequirement
import com.example.rfid_scanner.data.model.Tag
import com.example.rfid_scanner.databinding.ItemStockBinding
import com.example.rfid_scanner.utils.generic.adapter.GenericCustomAdapter
import com.example.rfid_scanner.utils.helper.ColorHelper.gColor
import com.example.rfid_scanner.utils.helper.TextHelper
import com.example.rfid_scanner.utils.helper.TextHelper.emptyString

class StockViewHolder {

    companion object {
        const val addOrUpdateStock = 0
        const val addTagsToStock = 1
        const val clearStockTags = 2
    }

    fun getAdapter(): GenericCustomAdapter<StockRequirement> {
        val adapter = GenericCustomAdapter<StockRequirement>()
        with(adapter) {
            expressionOnCreateViewHolder = {
                ItemStockBinding.inflate(LayoutInflater.from(it.context), it, false)
            }

            expressionViewHolderBinding = { item, viewBinding, _ ->
                val view = viewBinding as ItemStockBinding
                with(view) {
                    val quantityText = "${item.stock.itemQuantity} / ${item.reqQuantity}"

                    tvItemCode.text = item.stock.code
                    tvItemQuantity.text = quantityText

                    if (item.stock.isNameNull) {
                        tvItemName.text = "Kode barang tidak terdaftar"

                        llViewHolder.setBackgroundColor(gColor(R.color.dark_gray_item_disable))

                        val textColor = gColor(R.color.dark_gray_item_text_disable)
                        tvItemName.setTextColor(textColor)
                        tvItemCode.setTextColor(textColor)
                        tvItemQuantity.setTextColor(textColor)
                    } else {
                        tvItemName.text = item.stock.name

                        val reqQuantity = item.reqQuantity
                        val scanQuantity = item.stock.itemQuantity
                        llViewHolder.setBackgroundColor(gColor(
                            when {
                                reqQuantity == scanQuantity -> R.color.green_item_ok
                                reqQuantity < scanQuantity -> R.color.red_item_error
                                else -> R.color.light_gray_item_default
                            }
                        ))

                        val textColor = gColor(R.color.black_item_text_default)
                        tvItemName.setTextColor(textColor)
                        tvItemCode.setTextColor(textColor)
                        tvItemQuantity.setTextColor(textColor)
                    }
                }
            }

            expressionGetAdapterError = {
                var error: String? = null
                for (rStock in dataSet) {
                    when {
                        rStock.stock.isNameNull -> error = "Beberapa barang tidak terdaftar"
                        rStock.reqQuantity > rStock.stock.itemQuantity -> error = "Jumlah barang lebih sedikit dari yang dibutuhkan"
                        rStock.reqQuantity < rStock.stock.itemQuantity -> error = "Jumlah barang lebih banyak dari yang dibutuhkan"
                    }
                    if (error != null) break
                }
                error
            }

            /**
             * Custom operations start heere
             */
            mapOfOperations[addOrUpdateStock] = fun(rStock: StockRequirement) {
                mapOfIndex[rStock.stock.code]
                    ?.let {
                        dataSet[it].incQuantity(rStock.reqQuantity)
                        notifyItemChanged(it)
                    }
                    ?: run {
                        dataSet.add(rStock)
                        mapOfIndex[rStock.stock.code] = dataSet.lastIndex
                        notifyItemInserted(dataSet.lastIndex)
                    }
            }

            mapOfOperations[addTagsToStock] = fun(tag: Tag) {
                mapOfIndex[tag.stockCode]?.let {
                    dataSet[it].stock.addItem(tag.epc, tag.stockUnitCount)
                    notifyItemChanged(it)
                }
            }

            mapOfOperations[clearStockTags] = fun() {
                dataSet.mapIndexed { pos, stock ->
                    stock.stock.resetItem()
                    notifyItemChanged(pos)
                }
            }
        }

        return adapter
    }

}