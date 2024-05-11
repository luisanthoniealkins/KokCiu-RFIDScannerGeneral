package com.example.rfid_scanner.module.main.scan.non_transaction.check_detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import com.example.rfid_scanner.R
import com.example.rfid_scanner.data.model.Transaction
import com.example.rfid_scanner.databinding.FragmentCheckTagDetailBinding
import com.example.rfid_scanner.module.main.scan.non_transaction.check_detail.CheckTagDetailViewModel.Companion.JSON_KEY
import com.example.rfid_scanner.module.main.scan.non_transaction.check_detail.CheckTagDetailViewModel.Companion.dateFormat
import com.example.rfid_scanner.utils.generic.fragment.ScanFragment
import com.example.rfid_scanner.utils.helper.DateHelper
import java.util.*

class CheckTagDetailFragment : ScanFragment<FragmentCheckTagDetailBinding, CheckTagDetailViewModel>() {

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCheckTagDetailBinding.inflate(inflater, container, false)

    override fun getViewModelClass() = CheckTagDetailViewModel::class.java

    override fun getScanButton() = binding.btnScan
    override fun getResetButton(): Button? = null
    override fun getNonScanButtons(): List<Button> = listOf()

    override fun retrieveArgs() {
        val args : CheckTagDetailFragmentArgs by navArgs()
        viewModel.setSearchTag(args.searchTag)
    }

    override fun setUpViews() = with(binding) {
        if (viewModel.isSearching) {
            cvTarget.visibility = View.VISIBLE
            tvTagCodeTarget.text = viewModel.currentSearchTag
        }

        btnSort.text = if (viewModel.sortAscending) "Urutan naik" else "Urutan turun"
        btnSort.setOnClickListener {
            viewModel.sortAscending = !viewModel.sortAscending 
            btnSort.text = if (viewModel.sortAscending) "Urutan naik" else "Urutan turun"
            postProcessView(null)
        }
    }

    override fun observeData() = with(viewModel) {
        scanStatus.observeWithOwner {
            updateUIButton(it)
            if (!it.isPressing) viewModel.tagScanned = false
            if (it.isScanning && !viewModel.tagScanned) clearView()
        }

        lvTagScanned.observeWithOwner {
            if (isSearching) {
                if (it == currentSearchTag) {
                    binding.llTagTarget.setBackgroundColor(gColor(R.color.green_connect))
                    showToast("Kode RFID sesuai")
                } else {
                    binding.llTagTarget.setBackgroundColor(gColor(R.color.red_disconnect))
                    showToast("Kode RFID tidak sesuai")
                }
            }

            // TODO: PERLU LANGSUNG CEK ??
//            stopInventory()
//            binding.btnScan.isEnabled = false
        }

        lvViews.observeWithOwner { postProcessView(it) }
    }

    private fun clearView() = with(binding) {
        viewModel.transactions.clear()
        llTransactionHeader.visibility = View.GONE
        cvTag.visibility = View.GONE
        cvStock.visibility = View.GONE
        cvCheckIn.visibility = View.GONE
        cvCheckOut.visibility = View.GONE
        cvReturn.visibility = View.GONE
        cvBroken.visibility = View.GONE
        cvLost.visibility = View.GONE
        tvMessage.visibility = View.VISIBLE
    }

    private fun postProcessView(views: HashMap<String,HashMap<String,String>>?) = with(binding) {
        if (views != null) {
            clearView()
            tvMessage.visibility = View.GONE
            for (key in JSON_KEY) {
                if (!views.containsKey(key)) continue
                val property = Objects.requireNonNull<HashMap<String, String>>(views[key])
                var code: String?
                var customer: String?
                var delivery: String?
                var date: Date?
                when (key) {
                    "rfid" -> {
                        cvTag.visibility = View.VISIBLE
                        tvTagCode.text = property["rfid_code"]
                        tvTagStatus.text = property["rfid_status"]
                    }
                    "stock" -> {
                        cvStock.visibility = View.VISIBLE
                        llTransactionHeader.visibility = View.VISIBLE
                        tvStockId.text = property["stock_id"]
                        tvStockUnitCount.text = property["stock_unit_count"]
                        tvStockCode.text = property["stock_code"]
                        tvStockName.text = property["stock_name"]
                        tvStockBrand.text = property["stock_brand"]
                        tvStockVehicleType.text = property["stock_vehicle_type"]
                    }
                    "check_in" -> {
                        cvCheckIn.visibility = View.VISIBLE
                        code = property["bill_code"]
                        date = DateHelper.getDate("yyyy-MM-dd hh:mm:ss", property["bill_date"])
                        tvCheckInCode.text = code
                        tvCheckInDate.text = DateHelper.getFormattedDateTime(dateFormat, date!!)
                        viewModel.transactions.add(Transaction.CheckIn(code, date))
                    }
                    "check_out" -> {
                        cvCheckOut.visibility = View.VISIBLE
                        code = property["bill_code"]
                        date = DateHelper.getDate("yyyy-MM-dd hh:mm:ss", property["bill_date"])
                        customer = property["bill_customer"]
                        delivery = property["bill_delivery"]
                        tvCheckOutCode.text = code
                        tvCheckOutDate.text = DateHelper.getFormattedDateTime(dateFormat, date!!)
                        tvCheckOutCustomer.text = customer
                        tvCheckOutDelivery.text = delivery
                        viewModel.transactions.add(
                            Transaction.CheckOut(
                                code,
                                date,
                                customer!!,
                                delivery!!
                            )
                        )
                    }
                    "return" -> {
                        cvReturn.visibility = View.VISIBLE
                        code = property["bill_code"]
                        date = DateHelper.getDate("yyyy-MM-dd hh:mm:ss", property["bill_date"])
                        tvReturnCode.text = code
                        tvReturnDate.text = DateHelper.getFormattedDateTime(dateFormat, date!!)
                        viewModel.transactions.add(Transaction.Return(code, date))
                    }
                    "broken" -> {
                        cvBroken.visibility = View.VISIBLE
                        code = property["bill_code"]
                        date = DateHelper.getDate("yyyy-MM-dd hh:mm:ss", property["bill_date"])
                        tvBrokenCode.text = code
                        tvBrokenDate.text = DateHelper.getFormattedDateTime(dateFormat, date!!)
                        viewModel.transactions.add(Transaction.Broken(code, date))
                    }
                    "lost" -> {
                        cvLost.visibility = View.VISIBLE
                        code = property["bill_code"]
                        date = DateHelper.getDate("yyyy-MM-dd hh:mm:ss", property["bill_date"])
                        tvLostCode.text = code
                        tvLostDate.text = DateHelper.getFormattedDateTime(dateFormat, date!!)
                        viewModel.transactions.add(Transaction.Lost(code, date))
                    }
                }
            }
        }


        if (viewModel.sortAscending) {
            viewModel.transactions.sortBy { it.date }
        } else {
            viewModel.transactions.sortByDescending { it.date }
        }

        var lastView: View? = llTransactionHeader
        for (tr in viewModel.transactions) {
            var lp: RelativeLayout.LayoutParams
            var nextView: View? = null
            when (tr.type) {
                Transaction.STATUS_MASUK -> nextView = cvCheckIn
                Transaction.STATUS_KELUAR -> nextView = cvCheckOut
                Transaction.STATUS_RETUR -> nextView = cvReturn
                Transaction.STATUS_RUSAK -> nextView = cvBroken
                Transaction.STATUS_PENYESUAIAN -> nextView = cvLost
            }
            lp = nextView?.layoutParams as RelativeLayout.LayoutParams
            lp.addRule(RelativeLayout.BELOW, lastView!!.id)
            lastView = nextView
        }
    }

}