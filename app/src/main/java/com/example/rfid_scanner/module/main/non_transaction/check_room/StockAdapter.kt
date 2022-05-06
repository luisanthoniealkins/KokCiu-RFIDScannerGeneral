package com.example.rfid_scanner.module.main.non_transaction.check_room

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.rfid_scanner.R
import com.example.rfid_scanner.data.model.StockRequirement
import com.example.rfid_scanner.data.model.Tag
import com.example.rfid_scanner.databinding.ItemStockBinding

class StockAdapter: RecyclerView.Adapter<StockAdapter.StockVH>() {

    private val dataListFull = mutableListOf<StockRequirement>()
    private var dataList = listOf<StockRequirement>()

    private var showOK = false
    private var showZero = false


    override fun onCreateViewHolder(parent: ViewGroup, i: Int) =
        StockVH(ItemStockBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(tagVH: StockVH, position: Int) {
        if (showOK && showZero) tagVH.bind(dataListFull[position])
        else tagVH.bind(dataList[position])
    }

    override fun getItemCount() = if (showOK && showZero) dataListFull.size else dataList.size

    fun clearTags() {
        dataListFull.map { it.stock.resetItem() }
        refresh()
    }

    fun addStocks(stocks: List<StockRequirement>) {
        stocks.map { sReq ->
            dataListFull.firstOrNull { sReq.stock.code == it.stock.code }
                ?.incQuantity(sReq.reqQuantity)
                ?: run {
                    dataListFull.add(sReq)
                }
        }
        refresh()
    }

    fun addItems(tags: List<Tag>) {
        tags.map { tag ->
            dataListFull.firstOrNull { tag.stockCode == it.stock.code }?.stock?.addItem(
                tag.epc,
                tag.stockUnitCount
            )
        }
        refresh()
    }

    fun setShowOK(showOK: Boolean) {
        this.showOK = showOK
        refresh()
    }

    fun setShowZero(showZero: Boolean) {
        this.showZero = showZero
        refresh()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refresh() {
        filter()
        notifyDataSetChanged()
    }

    /** only for checkout */
//    val stocks: List<StockRequirement>
//        get() = dataListFull

    /** only for checkout */
    // 0 ok, 1 unknown, 2 less, 3 more
//    val error: String?
//        get() {
//            for (stock in mStocksFull) {
//                if (stock.isNameNull()) return "Beberapa barang tidak terdaftar"
//                if (stock.getReqQuantity() > stock.getItemQuantity()) return "Jumlah barang lebih sedikit yang dibutuhkan"
//                if (stock.getReqQuantity() < stock.getItemQuantity()) return "Jumlah barang lebih banyak yang dibutuhkan"
//            }
//            return null
//        }

    private fun filter() {
        if (showOK && showZero) return

        val stocks = mutableListOf<StockRequirement>()
        for (data in dataListFull) {
            if (!showZero && data.stock.itemQuantity == 0) continue
            if (!showOK && data.stock.itemQuantity == data.reqQuantity) continue
            stocks.add(data)
        }
        dataList = stocks
    }


    inner class StockVH(private val binding: ItemStockBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: StockRequirement) = with(binding) {

            val scanQuantity: Int = data.stock.itemQuantity
            val reqQuantity: Int = data.reqQuantity

            tvItemQuantity.text = ("$scanQuantity / $reqQuantity")
            tvItemCode.text = data.stock.code

            /** only for checkout */
            if (data.stock.isNameNull) {
                tvItemName.text = ("Kode barang tidak terdaftar")
//                mLLViewHolder.setBackgroundColor(
//                    ContextCompat.getColor(
//                        mContext,
//                        R.color.dark_gray_item_disable
//                    )
//                )
//                val color: Int =
//                    ContextCompat.getColor(mContext, R.color.dark_gray_item_text_disable)
//                mTVName.setTextColor(color)
//                mTVCode.setTextColor(color)
//                mTVQuantity.setTextColor(color)
            } else {
                tvItemName.text = data.stock.name
                binding.root.context?.let {
                    var color: Int = when {
                        reqQuantity == scanQuantity -> R.color.green_item_ok
                        reqQuantity < scanQuantity -> R.color.red_item_error
                        else -> R.color.light_gray_item_default
                    }

                    llViewHolder.setBackgroundColor(ContextCompat.getColor(it, color))
                    color = ContextCompat.getColor(it, R.color.black_item_text_default)

                    tvItemName.setTextColor(color)
                    tvItemCode.setTextColor(color)
                    tvItemQuantity.setTextColor(color)
                }


            }
        }
    }
}