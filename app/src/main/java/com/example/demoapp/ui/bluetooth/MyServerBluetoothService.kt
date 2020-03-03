package com.example.demoapp.ui.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.ContentValues
import android.os.Handler
import android.util.Log
import android.widget.TextView
import java.io.*
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

class MyServerBluetoothService(private val handler: Handler) {
    private var mHandleRequestThread: HandleRequestThread? = null
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    inner class HandleRequestThread(mmSocket: BluetoothSocket) : Thread() {
        private val mmInStream: InputStream = mmSocket.inputStream
        private val mmBuffer: ByteArray = ByteArray(1024) // mmBuffer store for the stream

        override fun run() {
            var numBytes: Int // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) { // Read from the InputStream
                numBytes = try {
                    val numberOfByte = mmInStream.read(mmBuffer)
                    //Creating a BufferedReader object
                    Log.e(ContentValues.TAG, mmBuffer.toString(Charset.defaultCharset()))
                } catch (e: IOException) {
                    Log.e(ContentValues.TAG, "Error reading Input Stream. " + e.message)
                    break
                }
                val readMsg = handler.obtainMessage(
                    MESSAGE_READ, numBytes, -1,
                    mmBuffer
                )
                readMsg.sendToTarget()
            }
        }
    }

    private fun manageMyConnectedSocket(mmSocket: BluetoothSocket) {
        Log.d(ContentValues.TAG, "connected: Starting.")
        // Start the thread to manage the connection and perform transmissions
        mHandleRequestThread = HandleRequestThread(mmSocket)
        mHandleRequestThread!!.run()
    }

    inner class AcceptThread : Thread() {
        private val mmServerSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
            bluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord(NAME, MY_UUID)
        }

        override fun run() {
            Log.d(ContentValues.TAG, "run: AcceptThread Running")
            var shouldLoop = true
            while (shouldLoop) {
                val socket: BluetoothSocket? = try {
                    mmServerSocket?.accept()
                } catch (e: IOException) {
                    Log.e(ContentValues.TAG, "Socket's accept() method failed", e)
                    shouldLoop = false
                    null
                }
                socket?.also {
                    manageMyConnectedSocket(it)
                    mmServerSocket?.close()
                    shouldLoop = false
                }
            }
        }

        fun cancel() {
            Log.d(ContentValues.TAG, "cancel: Canceling AcceptThread.")
            try {
                mmServerSocket?.close()
            } catch (e: IOException) {
                Log.e(
                    ContentValues.TAG,
                    "cancel: Close of AcceptThread ServerSocket failed. " + e.message
                )
            }
        }
    }

    fun startServer() {
        val accept = AcceptThread()
        accept.start()
    }
}