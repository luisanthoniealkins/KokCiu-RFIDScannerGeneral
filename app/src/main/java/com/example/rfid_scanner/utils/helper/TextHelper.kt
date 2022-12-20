package com.example.rfid_scanner.utils.helper

object TextHelper {

    const val defaultEmptyString = "-"

    fun emptyString() = ""

    fun getIdFromCodeAndUnitCount(code: String, unitCount: Int): String {
        return "$code#Q$unitCount"
    }

    fun getIdFromCodeAndUnitCount(code: String, unitCount: CharSequence?): String {
        return "$code#Q$unitCount"
    }

}