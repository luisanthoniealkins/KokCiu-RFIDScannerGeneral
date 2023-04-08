package com.example.rfid_scanner.module.main.history.transaction.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rfid_scanner.R
import com.example.rfid_scanner.data.model.Bill
import com.example.rfid_scanner.data.model.Transaction
import com.example.rfid_scanner.data.model.Transaction.CheckOut
import com.example.rfid_scanner.databinding.ItemTransactionBinding
import com.example.rfid_scanner.utils.app.App
import com.example.rfid_scanner.utils.extension.StringExt.hasLowerCaseSubsequence
import com.example.rfid_scanner.utils.extension.StringExt.hasPattern
import com.example.rfid_scanner.utils.helper.DateHelper
import com.example.rfid_scanner.utils.listener.ItemClickListener
import java.util.*

class TransactionAdapter(
    var mTransactionsFull: List<Transaction>,
    var listener: ItemClickListener
): RecyclerView.Adapter<TransactionAdapter.TransactionHolder>(), Filterable {

    private var mTransactions = mutableListOf<Transaction>()
    private var checkedStatus = HashMap<String, Boolean>()
    private var textFilter = ""

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): TransactionHolder =
        TransactionHolder(ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(tagHolder: TransactionHolder, position: Int) =
        tagHolder.bind(mTransactions[position])

    override fun getItemCount(): Int = mTransactions.size

    fun setTransactions(transactions: List<Transaction>) {
        mTransactionsFull = transactions
        refresh()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refresh() {
        filter.filter(textFilter)
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

    override fun getFilter(): Filter {
        return codeFilter
    }

    private val codeFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList: MutableList<Transaction> = ArrayList()
            textFilter = ""
            for (tr in mTransactionsFull) {
                if (!checkedStatus[tr.type]!!) continue
                if (constraint.isNotEmpty()) {
                    val filterPattern =
                        constraint.toString().lowercase(Locale.getDefault()).trim { it <= ' ' }
                    textFilter = filterPattern.lowercase(Locale.getDefault())

                    var match = false
                    if ((tr.code!!.lowercase(Locale.getDefault()).hasLowerCaseSubsequence(textFilter)) ||
                        (tr.getFormattedDate("yyyy-MM-dd / hh:mm").hasLowerCaseSubsequence(textFilter)) ||
                        (tr.type == Transaction.STATUS_KELUAR && (tr as CheckOut).customer.hasLowerCaseSubsequence(textFilter))
                    ) match = true

                    tr.getDetails().map {
                        val str = "${it.stockName} ${it.stockCode} ${it.stockVehicleType}"
                        if (str.hasPattern(textFilter)) match = true
                        if (match) return@map
                    }

                    if (!match) continue
                }
                filteredList.add(tr)
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            mTransactions.clear()
            mTransactions.addAll(results.values as List<Transaction>)
            notifyDataSetChanged()
        }
    }

    inner class TransactionHolder(private val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            with(binding) {
                rvItem.layoutManager = LinearLayoutManager(root.context)
                rvItem.addItemDecoration(DividerItemDecoration(root.context, DividerItemDecoration.VERTICAL))
            }
        }

        fun bind(data: Transaction) = with(binding){
            tvCode.text = data.code
            tvDate.text = DateHelper.getFormattedDateTime("yyyy-MM-dd / hh:mm", data.date!!)
            if (data.type == Transaction.STATUS_KELUAR) {
                llCustomer.visibility = View.VISIBLE
                tvCustomer.text = (data as CheckOut).customer
            } else {
                llCustomer.visibility = View.GONE
                imvPrint.visibility = View.GONE
            }

            imvPrint.visibility = if (data.type == Transaction.STATUS_KELUAR)
                View.VISIBLE else View.GONE

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
                    },
                    null
                )
            )

            val adapter = TransactionDetailViewHolder(data.getDetails()).getAdapter()
            rvItem.adapter = adapter

            imvInfo.setOnClickListener { data.code?.let { code -> listener.onItemClick(code) } }

            imvPrint.setOnClickListener {
                data.code?.let {
                    val checkOutData = data as CheckOut
                    listener.onItemClick(
                        Bill(
                            it,
                            checkOutData.customer,
                            checkOutData.delivery
                        )
                    )
                }
            }
        }
    }
}