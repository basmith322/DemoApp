package com.example.demoapp.ui.bluetoothCommandManagement

import android.bluetooth.BluetoothDevice
import android.content.ContentValues
import android.util.Log
import com.example.demoapp.ui.bluetooth.BluetoothSettingsViewModel
import com.github.pires.obd.commands.protocol.ObdResetCommand
import com.github.pires.obd.commands.protocol.SelectProtocolCommand
import com.github.pires.obd.enums.ObdProtocols
import java.io.InputStream
import java.io.OutputStream

class BTSettingsCommandSender(
    device: BluetoothDevice,
    providedViewModel: BluetoothSettingsViewModel
) : AbstractCommandSender<BluetoothSettingsViewModel>(device, providedViewModel) {

    override fun performCommand(inputStream: InputStream, outputStream: OutputStream) {
        val obdResetCommand = ObdResetCommand()
        obdResetCommand.run(inputStream, outputStream)

        try {
            sleep(500)
        } catch (e: InterruptedException) {
            Log.e(ContentValues.TAG,"Error with OBD reset:", e)
        }

        val selectProtocolCommand = SelectProtocolCommand(ObdProtocols.AUTO)
        selectProtocolCommand.run(inputStream,outputStream)
        val protocolResult = selectProtocolCommand.result

        val bluetoothSettingsViewModel = viewModel
        bluetoothSettingsViewModel.returnedProtocol.postValue(protocolResult)
    }
}