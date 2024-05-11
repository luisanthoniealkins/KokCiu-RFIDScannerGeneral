package com.example.rfid_scanner.module.main.scan.transaction.checkout.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.rfid_scanner.databinding.DialogCheckoutConfirmationBinding
import com.example.rfid_scanner.utils.constant.Constant
import com.example.rfid_scanner.utils.constant.Constant.DialogResult.RESULT_POSITIVE
import com.example.rfid_scanner.utils.generic.dialog.BaseDialog
import com.example.rfid_scanner.utils.listener.DialogConfirmationListener

class CheckoutConfirmationDialog(
    private val tagCount: Int,
    val listener: DialogConfirmationListener,
) : BaseDialog<DialogCheckoutConfirmationBinding>() {

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        DialogCheckoutConfirmationBinding.inflate(inflater, container, false)

    override fun setUpViews() = with(binding) {
        tvTagCount.text = tagCount.toString()

        btnCancel.setOnClickListener { dismiss() }
        btnOk.setOnClickListener {
            listener.onDialogDismiss(RESULT_POSITIVE, null)
            dismiss()
        }
    }
}