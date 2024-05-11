package com.example.rfid_scanner.module.main.settings.bluetooth.adapter

import android.view.LayoutInflater
import android.view.View
import com.example.rfid_scanner.data.model.*
import com.example.rfid_scanner.databinding.ItemBtDeviceConfigBinding
import com.example.rfid_scanner.utils.generic.adapter.GenericAdapter

class BTDeviceConfigViewHolder(
    private val dataSet: MutableList<BTDeviceConfig>,
) {

    fun getAdapter(): GenericAdapter<BTDeviceConfig> {
        val adapter = GenericAdapter(dataSet)
        with(adapter) {
            expressionOnCreateViewHolder = {
                ItemBtDeviceConfigBinding.inflate(LayoutInflater.from(it.context), it, false)
            }

            expressionViewHolderBinding = { item, viewBinding, _ ->
                val view = viewBinding as ItemBtDeviceConfigBinding
                with(view) {
                    tvMacAddress.text = item.macAddress
                    tvDeviceType.text = item.deviceType.displayText

                    if (item.prefixCodeCut == BTDeviceConfig.defaultPrefixCodeCut()) {
                        trPrefixCodeCut.visibility = View.GONE
                    } else {
                        trPrefixCodeCut.visibility = View.VISIBLE
                        tvPrefixCodeCut.text = item.prefixCodeCut.toString()
                    }

                    if (item.suffixCodeCut == BTDeviceConfig.defaultSuffixCodeCut()) {
                        trSuffixCodeCut.visibility = View.GONE
                    } else {
                        trSuffixCodeCut.visibility = View.VISIBLE
                        tvSuffixCodeCut.text = item.suffixCodeCut.toString()
                    }

                    imvEdit.setOnClickListener {
                        listener?.onItemClick(item.macAddress)
                    }
                }
            }
        }

        return adapter
    }

}