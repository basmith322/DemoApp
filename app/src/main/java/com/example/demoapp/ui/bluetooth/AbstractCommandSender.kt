package com.example.demoapp.ui.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.demoapp.utilities.MY_UUID
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

abstract class AbstractCommandSender<T : ViewModel>
    (
    device: BluetoothDevice,
    providedViewModel: T
) : Thread() {
    private lateinit var input: InputStream
    protected var viewModel: T = providedViewModel
    private lateinit var output: OutputStream
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
        device.createRfcommSocketToServiceRecord(MY_UUID)
    }

    override fun run() {
        Log.d(ContentValues.TAG, "ConnectThread: started.")
        bluetoothAdapter?.cancelDiscovery()

        mmSocket?.use { socket ->
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            try {
                socket.connect()

                // The connection attempt succeeded. Perform work associated with
                // the connection in a separate thread.
                manageMyConnectedSocket(mmSocket!!)
            } catch (e: Exception) {
                Log.e(ContentValues.TAG, "Socket Connection Failed")
            }
        }
    }

    private fun manageMyConnectedSocket(mmSocket: BluetoothSocket) {
        input = mmSocket.inputStream
        output = mmSocket.outputStream
        performCommand(input, output)
    }

    protected abstract fun performCommand(inputStream: InputStream, outputStream: OutputStream)

    fun cancel() {
        val any = try {
            Log.d(ContentValues.TAG, "cancel: Closing Client Socket")
            mmSocket?.close()
        } catch (e: IOException) {
            Log.e(
                ContentValues.TAG,
                "cancel: close() of mmSocket in connectThread failed " + e.message
            )
        }
    }
}