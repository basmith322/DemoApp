package com.example.demoapp.utilities.bluetoothCommandManagement

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


/**An abstract command sender to provide a template for the other command senders to use
* Takes in parameters for BluetoothDevice to use for the socket commands and a view model to return
* values to*/
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

    //run socket command in separate thread as blocking calls will stop main thread if called there
    override fun run() {
        Log.d(ContentValues.TAG, "ConnectThread: started.")
        bluetoothAdapter?.cancelDiscovery()
        try {
            mmSocket?.use { socket ->
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                socket.connect()

                // The connection attempt succeeded. Perform work associated with
                // the connection in a separate thread.
                manageMyConnectedSocket(mmSocket!!)
            }
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Socket Connection Failed")
            cancel()
        }
    }

    private fun manageMyConnectedSocket(mmSocket: BluetoothSocket) {
        //Set up input and output streams, performs the command using the input and output streams
        input = mmSocket.inputStream
        output = mmSocket.outputStream
        performCommand(input, output)
    }

    //Abstract performance command to be implemented by classes that extend this class
    protected abstract fun performCommand(inputStream: InputStream, outputStream: OutputStream)

    //Cancel command called in the event of issue
    private fun cancel() {
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
}
