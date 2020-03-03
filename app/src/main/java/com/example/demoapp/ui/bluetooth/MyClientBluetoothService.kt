package com.example.demoapp.ui.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.ContentValues
import android.util.Log
import java.io.IOException
import java.nio.charset.Charset

class MyClientBluetoothService {
    private var connectionToServer: ConnectToServerThread? = null
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    inner class ConnectToServerThread(device: BluetoothDevice) : Thread() {
        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(MY_UUID)
        }

        override fun run() {
            Log.d(ContentValues.TAG, "ConnectThread: started.")
            bluetoothAdapter?.cancelDiscovery()

            mmSocket?.use { socket ->
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                socket.connect()

                // The connection attempt succeeded. Perform work associated with
                // the connection in a separate thread.
                manageMyConnectedSocket(mmSocket!!)
            }
        }

        private fun manageMyConnectedSocket(mmSocket: BluetoothSocket) {
            while (true){}
        }

        fun cancel() {
            try {
                Log.d(ContentValues.TAG, "cancel: Closing Client Socket")
                mmSocket?.close()
            } catch (e: IOException) {
                Log.e(
                    ContentValues.TAG,
                    "cancel: close() of mmSocket in connectThread failed " + e.message
                )
            }
        }

        fun write(bytes: ByteArray) {
            try {
                mmSocket!!.outputStream!!.write(bytes)
            } catch (e: IOException) {
                Log.e(ContentValues.TAG, "Error occurred when sending data", e)
            }
        }
    }

    fun writeMessage() {
        connectionToServer!!.write("Test".toByteArray(Charset.defaultCharset()))
    }

    fun connectToServer() {
        val pairedDevices =
            bluetoothAdapter?.bondedDevices
        if (pairedDevices != null) {
            Log.e("MainActivity", "" + pairedDevices.size)
            if (pairedDevices.size > 0) {
                val devices: Array<Any> = pairedDevices.toTypedArray()
                val device: BluetoothDevice = devices[0] as BluetoothDevice
                Log.e("MainActivity", "" + device)
//                Log.e("MAinActivity", "" + deviceUUID)
                connectionToServer = ConnectToServerThread(device)
                connectionToServer!!.start()
            }
        }
    }
}