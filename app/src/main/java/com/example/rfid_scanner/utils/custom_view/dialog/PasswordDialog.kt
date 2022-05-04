package com.example.rfid_scanner.utils.custom_view.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.widget.EditText
import android.os.Bundle
import com.example.rfid_scanner.R
import androidx.fragment.app.DialogFragment

class PasswordDialog(private val listener: PasswordDialogListener) : DialogFragment() {
    private var mEDTPassword: EditText? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_password, null)
        builder.setView(view)
            .setTitle("Admin")
            .setNegativeButton("cancel") { _, _ -> listener.reEnableButton() }
            .setPositiveButton("ok") { _, _ ->
                val password = mEDTPassword!!.text.toString()
                listener.submitPassword(password)
                listener.reEnableButton()
            }
        mEDTPassword = view.findViewById(R.id.id_dialog_password_edt_password)
        return builder.create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listener.reEnableButton()
    }

    interface PasswordDialogListener {
        fun submitPassword(password: String?)
        fun reEnableButton()
    }
}