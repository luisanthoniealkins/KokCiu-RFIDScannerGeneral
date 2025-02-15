package com.example.rfid_scanner.utils.helper

import com.example.rfid_scanner.utils.extension.StringExt.isNumberOnly
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

object InputVerificationHelper {

    fun verifyInput(edt: TextInputEditText, til: TextInputLayout, fs : List<(TextInputEditText, TextInputLayout) -> Boolean>) =
        if (fs.firstOrNull { !it(edt,til) } == null) {
            til.error = ""
            true
        } else false

    fun mustFilledInput(edt: TextInputEditText, til: TextInputLayout) =
        if (edt.text.toString().trim().isEmpty()) {
            til.error = "Kolom harus diisi"
            false
        } else true


    fun mustNumberInput(edt: TextInputEditText, til: TextInputLayout) =
        if (!edt.text.toString().trim().isNumberOnly()) {
            til.error = "Kolom harus diisi dengan angka"
            false
        } else true

    fun mustSmallNumberInput(edt: TextInputEditText, til: TextInputLayout) =
        if (edt.text.toString().trim().length > 6) {
            til.error = "Kolom harus diisi dengan angka maximum 6 digit"
            false
        } else true
}