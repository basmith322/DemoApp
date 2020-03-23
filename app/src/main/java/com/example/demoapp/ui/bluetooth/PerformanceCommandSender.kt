package com.example.demoapp.ui.bluetooth

import android.bluetooth.BluetoothDevice
import com.example.demoapp.ui.performance.PerformanceViewModel
import com.github.pires.obd.commands.SpeedCommand
import com.github.pires.obd.commands.engine.RPMCommand
import com.github.pires.obd.commands.pressure.BarometricPressureCommand
import java.io.InputStream
import java.io.OutputStream

class PerformanceCommandSender(device: BluetoothDevice, viewModel: PerformanceViewModel) :
    AbstractCommandSender<PerformanceViewModel>(device, viewModel) {

    override fun performCommand(inputStream: InputStream, outputStream: OutputStream) {
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
}