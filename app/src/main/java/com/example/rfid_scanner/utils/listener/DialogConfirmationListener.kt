package com.example.rfid_scanner.utils.listener

import com.example.rfid_scanner.utils.constant.Constant

interface DialogConfirmationListener {
    fun onDialogDismiss(result: Constant.DialogResult, item: Any?)
}