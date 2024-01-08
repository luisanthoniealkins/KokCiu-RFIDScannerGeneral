package com.example.rfid_scanner.utils.extension

import android.util.Log
import com.example.rfid_scanner.service.StorageService
import com.example.rfid_scanner.utils.extension.StringExt.isSimilarTo
import com.example.rfid_scanner.utils.helper.LogHelper
import com.example.rfid_scanner.utils.helper.TagHelper

object StringExt {

    fun String.hasLowerCaseSubsequence(pattern: String): Boolean {
        var p = 0
        this.lowercase().map { if (p < pattern.length && pattern[p] == it) p++ }
        return p == pattern.length
    }

    fun String.hasPattern(pattern: String): Boolean {
        val wordList = this.split(" ")
        pattern.split(" ").map {
            var found = false
            for (word in wordList) if (word.hasLowerCaseSubsequence(it)) {
                found = true
                break
            }
            if (!found) return false
        }
        return true
    }

    fun String.isLengthBetween(min: Int, max: Int): Boolean {
        return this.length in min..max
    }

    fun String.isSimilarTo(others: String, maxDif: Int): Boolean {
        if (this.length != others.length) return false
        var dif = 0
        this.mapIndexed { index, c -> if (others[index] != c) dif++ }
        return dif <= maxDif
    }

    fun List<String>.getSimilarStrings(): String {
        val set = mutableSetOf<String>()
        var result = ""
        this.map { str -> this.map { if (str != it) {
            if (str.isSimilarTo(it, StorageService.getI().epcDiffTolerance)) {
                if (!set.contains(str)) {
                    set.add(str)
                    result += str + "\n"
                }
                if (!set.contains(it)) {
                    set.add(it)
                    result += it + "\n"
                }
            }
        } } }
        return result
    }

    fun List<String>.getSimilarStringsTo(text: String): String {
        val set = mutableSetOf<String>()
        var result = ""
        this.map { str ->
            if (str.isSimilarTo(text, StorageService.getI().epcDiffTolerance)) {
                if (!set.contains(text)) {
                    set.add(text)
                    result += text + "\n"
                }
                if (!set.contains(str)) {
                    set.add(str)
                    result += str + "\n"
                }
            }
        }
        return result
    }

    fun String.isNumberOnly(): Boolean {
        for (x in this) if (x < '0' || x > '9') return false
        return true
    }
}