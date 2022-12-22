package com.example.rfid_scanner.module.main.history.stock.fragment.transaction.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.rfid_scanner.R
import com.example.rfid_scanner.data.model.Transaction
import com.example.rfid_scanner.data.model.Transaction.CheckOut
import com.example.rfid_scanner.databinding.ItemStockTransactionBinding
import com.example.rfid_scanner.utils.app.App
import com.example.rfid_scanner.utils.helper.DateHelper
import com.google.android.material.tabs.TabLayout.Tab

class StockTransactionAdapter(
    private var mTransactionsFull: List<Transaction>,
): RecyclerView.Adapter<StockTransactionAdapter.StockTransactionHolder>() {

    private var mTransactions = mutableListOf<Transaction>()
    private var checkedStatus = HashMap<String, Boolean>()
    private var mTab: Tab? = null

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): StockTransactionHolder =
        StockTransactionHolder(ItemStockTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(tagHolder: StockTransactionHolder, position: Int) =
        tagHolder.bind(mTransactions[position])

    override fun getItemCount(): Int = mTransactions.size

    fun setTransactions(transactions: List<Transaction>) {
        mTransactionsFull = transactions
        refresh()
    }

    fun setTab(tab: Tab?) {
        mTab = tab
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refresh() {
        filter()
        mTab?.let { it.text = "Transaksi (${mTransactions.size})" }
        notifyDataSetChanged()
    }

    fun setChecked(
        checkIn: Boolean,
        checkOut: Boolean,
        _return: Boolean,
        broken: Boolean,
        clear: Boolean,
        adjust: Boolean,
        other: Boolean
    ) {
        checkedStatus[Transaction.STATUS_MASUK] = checkIn
        checkedStatus[Transaction.STATUS_KELUAR] = checkOut
        checkedStatus[Transaction.STATUS_RETUR] = _return
        checkedStatus[Transaction.STATUS_RUSAK] = broken
        checkedStatus[Transaction.STATUS_HAPUS] = clear
        checkedStatus[Transaction.STATUS_PENYESUAIAN] = adjust
        checkedStatus[Transaction.STATUS_PAKAI_ULANG] = other
        checkedStatus[Transaction.STATUS_CUSTOM] = other
        refresh()
    }

    private fun filter() {
        mTransactions.clear()
        for (transaction in mTransactionsFull) {
            if (checkedStatus[transaction.type] == false) continue
            mTransactions.add(transaction)
        }
    }

    inner class StockTransactionHolder(private val binding: ItemStockTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Transaction) = with(binding) {
            tvCode.text = data.code
            tvDate.text = DateHelper.getFormattedDateTime("yyyy-MM-dd / hh:mm", data.date!!)
            tvQuantity.text = data.getQuantity().toString()

            llViewHolder.setBackgroundColor(
                ResourcesCompat.getColor(
                    App.res!!,
                    when (data.type) {
                        Transaction.STATUS_MASUK -> R.color.green_item_transaction_check_in
                        Transaction.STATUS_KELUAR -> R.color.light_red_transaction_check_out
                        Transaction.STATUS_RETUR -> R.color.blue_item_transaction_return
                        Transaction.STATUS_RUSAK -> R.color.light_brown_item_transaction_broken
                        Transaction.STATUS_HAPUS -> R.color.light_gray_item_transaction_clear
                        Transaction.STATUS_PENYESUAIAN -> R.color.light_yellow_item_transaction_adjust
                        else -> R.color.blue_gray_item_transaction_other
                    }, null
                )
            )

            when (data.type) {
                Transaction.STATUS_KELUAR -> {
                    llCustomer.visibility = View.VISIBLE
                    tvCustomer.text = (data as CheckOut).customer
                }
                else -> llCustomer.visibility = View.GONE
            }
        }
    }
}