package com.example.rfid_scanner.utils.helper

import android.content.Context
import android.widget.Toast

class ToastHelper {

    companion object {

        fun showToast(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }

    }

}
