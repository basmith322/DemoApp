package com.example.demoapp.utilities.bluetoothCommandManagement

import android.bluetooth.BluetoothDevice
import br.ufrn.imd.obd.commands.engine.RPMCommand
import br.ufrn.imd.obd.commands.engine.SpeedCommand
import br.ufrn.imd.obd.commands.pressure.BarometricPressureCommand
import br.ufrn.imd.obd.commands.protocol.TimeoutCommand
import com.example.demoapp.ui.performance.PerformanceViewModel
import java.io.InputStream
import java.io.OutputStream

class PerformanceCommandSender(device: BluetoothDevice, providedViewModel: PerformanceViewModel) :
    AbstractCommandSender<PerformanceViewModel>(device, providedViewModel) {

    override fun performCommand(inputStream: InputStream, outputStream: OutputStream) {
        val timeoutCommand = TimeoutCommand(62)
        timeoutCommand.run(inputStream,outputStream)

        val speedCommand = SpeedCommand()
        speedCommand.run(inputStream, outputStream)
        val speedResult = speedCommand.imperialSpeed.toInt()
        timeoutCommand.run(inputStream,outputStream)

        val rpmCommand = RPMCommand()
        rpmCommand.run(inputStream, outputStream)
        val rpmResult = rpmCommand.rpm
        timeoutCommand.run(inputStream,outputStream)

        val boostCommand = BarometricPressureCommand()
        boostCommand.run(inputStream, outputStream)
        val boostResult = boostCommand.imperialUnit.toInt()
        timeoutCommand.run(inputStream,outputStream)

        val pViewModel = viewModel
        pViewModel.currentSpeed.postValue(speedResult)
        pViewModel.currentRPM.postValue(rpmResult)
        pViewModel.currentBoost.postValue(boostResult)
    }
}