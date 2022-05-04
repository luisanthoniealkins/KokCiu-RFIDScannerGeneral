package com.example.rfid_scanner.utils.service

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rfid_scanner.data.model.TagEPC
import com.example.rfid_scanner.data.model.status.MConnectionStatus
import com.example.rfid_scanner.data.model.status.ScanStatus
import com.example.rfid_scanner.utils.constant.Constant.BTE_START_SCAN_COMMAND
import com.example.rfid_scanner.utils.constant.Constant.BTE_STOP_SCAN_COMMAND
import com.example.rfid_scanner.utils.generic.HandleEvent
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class BluetoothBTEService(context: Context, private val coroutineScope: CoroutineScope) {

    companion object {
        val allowedDelimiter = setOf('!','*','@')

        private const val TAG = "BLUETOOTH_SERVICE"
        private var mBTSocket: BluetoothSocket? = null // bi-directional client-to-client data path
        private val BT_MODULE_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // "random" unique identifier
    }

    private val _ldTags = MutableLiveData<HandleEvent<List<TagEPC>>>()
    val ldTags : LiveData<HandleEvent<List<TagEPC>>> = _ldTags

    private val _sfStatus = MutableStateFlow(MConnectionStatus())
    val sfStatus = _sfStatus.asStateFlow()

    private val _sfScanStatus = MutableStateFlow(ScanStatus())
    val sfScanStatus = _sfScanStatus.asStateFlow()

    private var mConnectedThread: ConnectedThread? = null
    private val mBTAdapter: BluetoothAdapter =
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter

    val isBluetoothEnabled: Boolean
        get() = mBTAdapter.isEnabled
    val isConnected: Boolean
        get() = mBTSocket?.isConnected ?: false

    var currentDevice: BluetoothDevice? = null
        private set

    private var isScanning = false
    private var isPressing = false

    init {
        updateStatus(true)
    }

    fun makeConnection(address: String?) {
        coroutineScope.launch {
            var isFailed = false
            val device: BluetoothDevice = mBTAdapter.getRemoteDevice(address)
            try {
                mBTSocket = createBluetoothSocket(device)
            } catch (e: IOException) {
                isFailed = true
                Log.e(TAG, "Socket creation failed")
            }

            // Establish the Bluetooth socket connection.
            try {
                kotlin.runCatching { mBTSocket?.connect() }
            } catch (e: IOException) {
                isFailed = true
                kotlin.runCatching { mBTSocket?.close() }
                Log.e(TAG, "Socket creation failed")
            }

            if (!isFailed) {
                mConnectedThread = ConnectedThread(mBTSocket)
                mConnectedThread?.start()
                currentDevice = device
                StorageService.getInstance().lastConnectedBluetoothDevice = device.address
            }
            updateStatus(isFailed)
        }
    }

    fun closeConnection() {
        stopScan()
        mConnectedThread?.cancel()
        updateStatus(true)
//        (mContext as MainActivity).runOnUiThread { (mContext as MainActivity).setBatteryLevel(0) }
    }

    private fun updateStatus(isFailed: Boolean) {
        _sfStatus.value = MConnectionStatus(isConnected, isFailed)
        updateScanStatus()
    }

    private fun updateScanStatus() {
        _sfScanStatus.value = ScanStatus(isConnected, isScanning, isPressing)
    }

    private inner class ConnectedThread(socket: BluetoothSocket?) : Thread() {
        private val mmSocket: BluetoothSocket? = socket
        private val mmInputStream: InputStream? = socket?.inputStream
        private val mmOutputStream: OutputStream? = socket?.outputStream
        override fun run() {
            var readBufferPosition = 0
            val mmReadBuffer = ByteArray(1024)

            // Keep listening to the InputStream until an exception occurs.\
            while (isConnected) {
                var bytesAvailable = 0
                try { bytesAvailable = mmInputStream?.available() ?: 0 }
                catch (e : java.lang.Exception) {}

                if (bytesAvailable == 0) continue
                val packetBytes = ByteArray(bytesAvailable)

                try { mmInputStream?.read(packetBytes) }
                catch (e : java.lang.Exception) {}

                var isProperData = false
                val tags = mutableListOf<TagEPC>()
                for (i in 0 until bytesAvailable) {
                    val b = packetBytes[i]

                    if (b == '#'.code.toByte()) {
                        isProperData = true
                        readBufferPosition = 0
                    } else if (isProperData) {
                        val bChar = b.toInt().toChar()
                        if (allowedDelimiter.contains(bChar)) {
                            var data = ""
                            repeat(readBufferPosition) { data += mmReadBuffer[it].toInt().toChar() }
                            when(bChar) {
                                '!' -> {
                                    if (isScanning) tags.add(TagEPC(data))
                                }
                                '*' -> {
//                                        if (NumberUtil.isNumeric(data)) {
//                                            val batteryLevel = data.toFloat()
//                                            if (0 <= batteryLevel && batteryLevel <= 100) (mContext as MainActivity).runOnUiThread {
//                                                (mContext as MainActivity).setBatteryLevel(
//                                                    batteryLevel
//                                                )
//                                            }
//                                        }
                                }
                                '@' -> {
                                    if (data == "O") {
                                        isScanning = true
                                        isPressing = true
                                    } else if (data == "C") {
                                        isScanning = false
                                        isPressing = false
                                    }
                                    updateScanStatus()
                                }
                            }

                            readBufferPosition = 0
                            isProperData = false
                        } else {
                            mmReadBuffer[readBufferPosition++] = b
                        }
                    }
                }

                if (tags.isNotEmpty()) _ldTags.postValue(HandleEvent(tags))
            }
        }

        // Call this from the main activity to send data to the remote device.
        fun write(message: String, scan: Boolean) {
            val bytes = message.toByteArray()
            isScanning = scan
            updateScanStatus()
            mmOutputStream?.write(bytes)
        }

        // Call this method from the main activity to shut down the connection.
        fun cancel() = mmSocket?.close()
    }

//    private suspend fun addTags(tags: MutableList<TagEPC>) {
//        channelTags.send(tags)
//    }

    @Throws(Exception::class)
    private fun createBluetoothSocket(device: BluetoothDevice): BluetoothSocket {
        //creates secure outgoing connection with BT device using UUID
        return device.createRfcommSocketToServiceRecord(BT_MODULE_UUID)
    }

    fun startScan() {
        if (isScanning) return
        mConnectedThread?.write(BTE_START_SCAN_COMMAND, true)
    }

    fun stopScan() {
        if (!isScanning || isPressing) return
        mConnectedThread?.write(BTE_STOP_SCAN_COMMAND, false)
    }

}