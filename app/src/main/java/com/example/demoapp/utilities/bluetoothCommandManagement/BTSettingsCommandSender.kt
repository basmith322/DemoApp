package com.example.demoapp.utilities.bluetoothCommandManagement

import android.bluetooth.BluetoothDevice
import com.example.demoapp.ui.bluetooth.BluetoothSettingsViewModel
import com.github.pires.obd.commands.protocol.SelectProtocolCommand
import com.github.pires.obd.commands.protocol.TimeoutCommand
import com.github.pires.obd.enums.ObdProtocols
import java.io.InputStream
import java.io.OutputStream

/** This class is used to establish the correct protocol to be used when selecting an OBD device
* in the Bluetooth Settings page */
class BTSettingsCommandSender(
    device: BluetoothDevice,
    providedViewModel: BluetoothSettingsViewModel
) : AbstractCommandSender<BluetoothSettingsViewModel>(device, providedViewModel) {

    override fun performCommand(inputStream: InputStream, outputStream: OutputStream) {
        //Short timeout command to ensure the OBD device is finished with any previous commands
        val timeoutCommand = TimeoutCommand(62)
        timeoutCommand.run(inputStream, outputStream)

        //Creating and running Select protocol command, storing the results in variables to be passed to view model
        val selectProtocolCommand = SelectProtocolCommand(ObdProtocols.AUTO)
        selectProtocolCommand.run(inputStream,outputStream)
        val protocolResult = selectProtocolCommand.result

        //Setting up view model and posting the values to the relevant mutable properties.
        val bluetoothSettingsViewModel = viewModel
        bluetoothSettingsViewModel.hasConnected = true
        bluetoothSettingsViewModel.returnedProtocol.postValue(protocolResult)
    }
}