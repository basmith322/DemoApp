package com.example.demoapp.ui.bluetoothCommandManagement

import android.bluetooth.BluetoothDevice
import android.content.ContentValues.TAG
import android.util.Log
import com.example.demoapp.ui.performance.PerformanceViewModel
import com.github.pires.obd.commands.SpeedCommand
import com.github.pires.obd.commands.engine.RPMCommand
import com.github.pires.obd.commands.pressure.BarometricPressureCommand
import com.github.pires.obd.commands.protocol.SelectProtocolCommand
import com.github.pires.obd.enums.ObdProtocols
import java.io.InputStream
import java.io.OutputStream

class PerformanceCommandSender(device: BluetoothDevice, providedViewModel: PerformanceViewModel) :
    AbstractCommandSender<PerformanceViewModel>(device, providedViewModel) {

    override fun performCommand(inputStream: InputStream, outputStream: OutputStream) {
        val selectProtocolCommand = SelectProtocolCommand(ObdProtocols.AUTO)
        try {
            selectProtocolCommand.run(inputStream,outputStream)
        }catch (e: Exception) {
            Log.e(TAG, "Error connecting protocol", e)
        }

        val speedCommand = SpeedCommand()
        speedCommand.run(inputStream, outputStream)
        val speedResult = speedCommand.formattedResult

        val rpmCommand = RPMCommand()
        rpmCommand.run(inputStream, outputStream)
        val rpmResult = rpmCommand.rpm

        val boostCommand = BarometricPressureCommand()
        boostCommand.run(inputStream, outputStream)
        val boostResult = boostCommand.imperialUnit.toInt()

        val pViewModel = viewModel
        pViewModel.currentSpeed.postValue(speedResult)
        pViewModel.currentRPM.postValue(rpmResult)
        pViewModel.currentBoost.postValue(boostResult)
    }
}