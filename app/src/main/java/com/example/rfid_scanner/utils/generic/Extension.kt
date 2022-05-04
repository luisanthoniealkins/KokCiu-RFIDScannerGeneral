package com.example.rfid_scanner.utils.generic

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.launch

class Extension {

    companion object {
        fun Fragment.getNavigationResult(key: String = "result") =
            findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>(key)

        fun Fragment.setNavigationResult(result: String, key: String = "result") {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(key, result)
        }

        fun <T> CoroutineScope.mergeChannels(vararg channels: ReceiveChannel<T>) : ReceiveChannel<T> {
            return produce {
                channels.forEach {
                    launch { it.consumeEach { send(it) }}
                }
            }
        }
    }

}