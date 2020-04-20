package com.example.demoapp.utilities.bluetoothCommandManagement

import android.bluetooth.BluetoothDevice
import com.example.demoapp.ui.setup.ui.ui.protocol.ProtocolViewModel
import com.github.pires.obd.commands.protocol.SelectProtocolCommand
import com.github.pires.obd.enums.ObdProtocols
import java.io.InputStream
import java.io.OutputStream

class ProtocolCommandSender(device: BluetoothDevice, providedViewModel: ProtocolViewModel) :
    AbstractCommandSender<ProtocolViewModel>(device, providedViewModel) {

    override fun performCommand(inputStream: InputStream, outputStream: OutputStream) {
        val selectProtocolCommand = SelectProtocolCommand(ObdProtocols.AUTO)
        selectProtocolCommand.run(inputStream,outputStream)
        val protocolResult = selectProtocolCommand.result

        val prViewModel = viewModel
        prViewModel.returnedProtocol.postValue(protocolResult)
    }
}