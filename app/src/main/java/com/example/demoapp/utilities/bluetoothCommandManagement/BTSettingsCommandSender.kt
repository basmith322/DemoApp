package com.example.demoapp.utilities.bluetoothCommandManagement

import android.bluetooth.BluetoothDevice
import com.example.demoapp.ui.bluetooth.BluetoothSettingsViewModel
import com.github.pires.obd.commands.protocol.SelectProtocolCommand
import com.github.pires.obd.enums.ObdProtocols
import java.io.InputStream
import java.io.OutputStream

class BTSettingsCommandSender(
    device: BluetoothDevice,
    providedViewModel: BluetoothSettingsViewModel
) : AbstractCommandSender<BluetoothSettingsViewModel>(device, providedViewModel) {

    override fun performCommand(inputStream: InputStream, outputStream: OutputStream) {
        val selectProtocolCommand = SelectProtocolCommand(ObdProtocols.AUTO)
        selectProtocolCommand.run(inputStream,outputStream)
        val protocolResult = selectProtocolCommand.result

        val bluetoothSettingsViewModel = viewModel
        bluetoothSettingsViewModel.returnedProtocol.postValue(protocolResult)
    }
}