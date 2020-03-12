package com.example.demoapp.utilities

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.demoapp.R
import com.example.demoapp.ui.bluetooth.BluetoothFragment
import com.example.demoapp.ui.performance.PerformanceViewModel
import com.github.pires.obd.commands.SpeedCommand
import com.github.pires.obd.commands.engine.RPMCommand
import com.github.pires.obd.commands.pressure.BarometricPressureCommand
import kotlinx.android.synthetic.main.fragment_bluetooth.view.*
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.util.*

var MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
class MyClientBluetoothService {
    private var connectionToServer: ConnectToServerThread? = null
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    inner class ConnectToServerThread(device: BluetoothDevice, performanceViewModel: PerformanceViewModel) : Thread() {
        private lateinit var input: InputStream
        private var viewModel: PerformanceViewModel = performanceViewModel
        private lateinit var output: OutputStream
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
                manageMyConnectedSocket(mmSocket!!)
            }
        }

        private fun manageMyConnectedSocket(mmSocket: BluetoothSocket) {
            input = mmSocket.inputStream
            output = mmSocket.outputStream
            performanceCommands(input,output)
        }

        private fun performanceCommands(inputStream: InputStream, outputStream: OutputStream) {
            val speedCommand = SpeedCommand()
            speedCommand.run(inputStream,outputStream)
            val speedResult:String = speedCommand.imperialSpeed.toInt().toString() + " MPH"

            val rpmCommand = RPMCommand()
            rpmCommand.run(inputStream,outputStream)
            val rpmResult = rpmCommand.calculatedResult + " RPM"

            val boostCommand = BarometricPressureCommand()
            boostCommand.run(inputStream,outputStream)
            val boostResult = boostCommand.imperialUnit.toInt().toString() + " PSI"

            val pViewModel = viewModel
            pViewModel.currentSpeed.postValue(speedResult)
            pViewModel.currentRPM.postValue(rpmResult)
            pViewModel.currentBoost.postValue(boostResult)
        }

        fun cancel() {
            val any = try {
                Log.d(TAG, "cancel: Closing Client Socket")
                mmSocket?.close()
            } catch (e: IOException) {
                Log.e(
                    TAG,
                    "cancel: close() of mmSocket in connectThread failed " + e.message
                )
            }
        }

        fun write(bytes: ByteArray) {
            try {
                mmSocket!!.outputStream!!.write(bytes)
            } catch (e: IOException) {
                Log.e(TAG, "Error occurred when sending data", e)
            }
        }
    }

    fun writeMessage() {
        connectionToServer!!.write("Test".toByteArray(Charset.defaultCharset()))
    }

    fun connectToServer(performanceViewModel: PerformanceViewModel) {
        val pairedDevices =
            bluetoothAdapter?.bondedDevices
        if (pairedDevices != null) {
            Log.e("MainActivity", "" + pairedDevices.size)
            if (pairedDevices.size > 0) {
                val devices: Array<Any> = pairedDevices.toTypedArray()
                val device: BluetoothDevice = devices[0] as BluetoothDevice
                Log.e("MainActivity", "" + device)
//                Log.e("MAinActivity", "" + deviceUUID)
                connectionToServer = ConnectToServerThread(device, performanceViewModel)
                connectionToServer!!.start()
            }
        }
    }
}