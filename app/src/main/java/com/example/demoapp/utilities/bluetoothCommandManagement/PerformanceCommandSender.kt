package com.example.demoapp.utilities.bluetoothCommandManagement

import android.bluetooth.BluetoothDevice
import com.example.demoapp.ui.performance.PerformanceViewModel
import com.github.pires.obd.commands.SpeedCommand
import com.github.pires.obd.commands.engine.RPMCommand
import com.github.pires.obd.commands.pressure.BarometricPressureCommand
import com.github.pires.obd.commands.protocol.TimeoutCommand
import java.io.InputStream
import java.io.OutputStream

/** This class is used to send the relevant commands for the Performance page and return the values
* to the performance view model where the observer can see the changes*/
class PerformanceCommandSender(device: BluetoothDevice, providedViewModel: PerformanceViewModel) :
    AbstractCommandSender<PerformanceViewModel>(device, providedViewModel) {

    override fun performCommand(inputStream: InputStream, outputStream: OutputStream) {
        //Short timeout command to ensure the OBD device is finished with any previous commands
        val timeoutCommand = TimeoutCommand(255)
        timeoutCommand.run(inputStream, outputStream)

        //Creating and running commands, storing the results in variables to be passed to view model
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

        //Setting up view model and posting the values to the relevant mutable properties.
        val pViewModel = viewModel
        pViewModel.currentSpeed.postValue(speedResult)
        pViewModel.currentRPM.postValue(rpmResult)
        pViewModel.currentBoost.postValue(boostResult)
    }
}