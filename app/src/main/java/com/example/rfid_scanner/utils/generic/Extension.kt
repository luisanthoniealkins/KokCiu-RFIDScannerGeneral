package com.example.rfid_scanner.utils.generic

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.launch

object Extension {

    fun String.hasLowerCaseSubsequence(pattern: String): Boolean {
        var p = 0
        this.lowercase().map { if (p < pattern.length && pattern[p] == it) p++ }
        return p == pattern.length
    }




}