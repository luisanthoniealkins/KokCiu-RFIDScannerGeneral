package com.example.rfid_scanner.utils.helper

object NumberUtil {

    fun isNumeric(s: String): Boolean {
        for (c in s.toCharArray()) if (!(c in '0'..'9' || c == '.')) return false
        return true
    }

    fun isBetweenIntRange(s: String): Boolean {
        return try {
            s.toInt()
            true
        } catch (e: java.lang.Exception) {
            false
        }
    }

}