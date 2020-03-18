package com.example.demoapp.utilities

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.ContentValues.TAG
import android.util.Log
import com.example.demoapp.ui.performance.PerformanceViewModel
import com.github.pires.obd.commands.SpeedCommand
import com.github.pires.obd.commands.engine.RPMCommand
import com.github.pires.obd.commands.pressure.BarometricPressureCommand
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

var MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

class MyClientBluetoothService {
    private var connectionToServer: ConnectToServerThread? = null
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    inner class ConnectToServerThread(
        device: BluetoothDevice,
        performanceViewModel: PerformanceViewModel
    ) : Thread() {
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
                try {
                    socket.connect()

                    // The connection attempt succeeded. Perform work associated with
                    // the connection in a separate thread.
                    manageMyConnectedSocket(mmSocket!!)
                } catch (e: Exception) {
                    Log.e(TAG, "Socket Connection Failed")
                }
            }
        }

        private fun manageMyConnectedSocket(mmSocket: BluetoothSocket) {
            input = mmSocket.inputStream
            output = mmSocket.outputStream
            performanceCommands(input, output)
        }

        private fun performanceCommands(inputStream: InputStream, outputStream: OutputStream) {
            val speedCommand = SpeedCommand()
            speedCommand.run(inputStream, outputStream)
            val speedResult: String = speedCommand.imperialSpeed.toInt().toString() + " MPH"

            val rpmCommand = RPMCommand()
            rpmCommand.run(inputStream, outputStream)
            val rpmResult = rpmCommand.calculatedResult + " RPM"

            val boostCommand = BarometricPressureCommand()
            boostCommand.run(inputStream, outputStream)
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
    }

    fun connectToServer(performanceViewModel: PerformanceViewModel, device: BluetoothDevice) {
        connectionToServer = ConnectToServerThread(device, performanceViewModel)
        connectionToServer!!.start()
    }
}