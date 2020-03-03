package com.example.demoapp.ui.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.TextView
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.util.*

const val MESSAGE_READ: Int = 0
const val MESSAGE_WRITE: Int = 1
const val MESSAGE_TOAST: Int = 2
const val REQUEST_ENABLE_BT = 1
const val NAME: String = "Device"
var send_data: String = "Test"
lateinit var view_data: TextView
var MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

class MyBluetoothService(private val handler: Handler) {
    private var mConnectedThread: ConnectedThread? = null
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    inner class ConnectedThread(mmSocket: BluetoothSocket) : Thread() {
        private val mmInStream: InputStream = mmSocket.inputStream
        private val mmOutStream: OutputStream = mmSocket.outputStream
        private val mmBuffer: ByteArray = ByteArray(1024) // mmBuffer store for the stream

        override fun run() {
            var numBytes: Int // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) { // Read from the InputStream
                numBytes = try {
                    mmInStream.read(mmBuffer)
                } catch (e: IOException) {
                    Log.e(TAG, "Error reading Input Stream. " + e.message)
                    break
                }
                val readMsg = handler.obtainMessage(
                    MESSAGE_READ, numBytes, -1,
                    mmBuffer
                )
                readMsg.sendToTarget()
            }
        }

        fun write(bytes: ByteArray) {
            try {
                mmOutStream.write(bytes)
            } catch (e: IOException) {
                Log.e(TAG, "Error occurred when sending data", e)

                // Send a failure message back to the activity.
                val writeErrorMsg = handler.obtainMessage(MESSAGE_TOAST)
                val bundle = Bundle().apply {
                    putString("toast", "Couldn't send data to the other device")
                }
                writeErrorMsg.data = bundle
                handler.sendMessage(writeErrorMsg)
                return
            }

            // Share the sent message with the UI activity.
            val writtenMsg = handler.obtainMessage(
                MESSAGE_WRITE, -1, -1, mmBuffer
            )
            writtenMsg.sendToTarget()
        }
    }

    inner class ConnectThread(device: BluetoothDevice) : Thread() {
        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(MY_UUID)
        }

        override fun run() {
            Log.d(TAG, "ConnectThread: started.")
            bluetoothAdapter?.cancelDiscovery()

            mmSocket?.use { socket ->
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                socket.connect()

                // The connection attempt succeeded. Perform work associated with
                // the connection in a separate thread.
                connected(mmSocket!!)
            }
        }

        fun cancel() {
            try {
                Log.d(TAG, "cancel: Closing Client Socket")
                mmSocket?.close()
            } catch (e: IOException) {
                Log.e(
                    TAG,
                    "cancel: close() of mmSocket in connectThread failed " + e.message
                )
            }
        }
    }

    private fun connected(mmSocket: BluetoothSocket) {
        Log.d(TAG, "connected: Starting.")
        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = ConnectedThread(mmSocket)
        mConnectedThread!!.start()
    }

    inner class AcceptThread : Thread() {
        private val mmServerSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
            bluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord(NAME, MY_UUID)
        }

        override fun run() {
            Log.d(TAG, "run: AcceptThread Running")
            var shouldLoop = true
            while (shouldLoop) {
                val socket: BluetoothSocket? = try {
                    mmServerSocket?.accept()
                } catch (e: IOException) {
                    Log.e(TAG, "Socket's accept() method failed", e)
                    shouldLoop = false
                    null
                }
                socket?.also {
                    connected(it)
                    Thread.sleep(1000)
                    mmServerSocket?.close()
                    shouldLoop = false
                }
            }
        }

        fun cancel() {
            Log.d(TAG, "cancel: Canceling AcceptThread.")
            try {
                mmServerSocket?.close()
            } catch (e: IOException) {
                Log.e(
                    TAG,
                    "cancel: Close of AcceptThread ServerSocket failed. " + e.message
                )
            }
        }
    }

    fun sendMessage() {
        val bytes: ByteArray = send_data.toByteArray(Charset.defaultCharset())
        mConnectedThread?.write(bytes)
    }

    fun startServer() {
        val accept = AcceptThread()
        accept.start()
    }

    fun pairDevice() {
        val pairedDevices =
            bluetoothAdapter?.bondedDevices
        if (pairedDevices != null) {
            Log.e("MAinActivity", "" + pairedDevices.size)
            if (pairedDevices.size > 0) {
                val devices: Array<Any> = pairedDevices.toTypedArray()
                val device: BluetoothDevice = devices[0] as BluetoothDevice
//                var list = device.uuids
//                var deviceUUID = UUID.fromString(list[0].toString())
                Log.e("MainActivity", "" + device)
//                Log.e("MAinActivity", "" + deviceUUID)
                val connect = ConnectThread(device)
                connect.start()
            }
        }
    }
}
