package com.example.rfid_scanner.utils.helper

import android.annotation.SuppressLint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateHelper {
    val currentDate: Date
        get() = Calendar.getInstance().time

    @SuppressLint("SimpleDateFormat")
    fun getFormattedDateTimeCurrent(format: String?): String {
        return getFormattedDateTime(format, Calendar.getInstance().time)
    }

    @SuppressLint("SimpleDateFormat")
    fun getFormattedDateTime1(date: Date): String {
        return SimpleDateFormat("yyyy-MM-dd").format(date.time) +
                SimpleDateFormat(" HH:mm:ss").format(Calendar.getInstance().time)
    }

    @SuppressLint("SimpleDateFormat")
    fun getDate(format: String?, date: String?): Date? {
        return try {
            SimpleDateFormat(format).parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getFormattedDateTime(format: String?, date: Date): String {
        return SimpleDateFormat(format).format(date.time)
    }

    fun getDateAttribute(date: Date?, attr: Int): Int {
        if (date == null) return 1
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar[attr]
    }
}