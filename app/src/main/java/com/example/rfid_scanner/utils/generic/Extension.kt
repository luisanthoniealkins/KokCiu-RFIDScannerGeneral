package com.example.rfid_scanner.utils.generic

object Extension {

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


}