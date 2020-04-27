package com.example.demoapp.utilities.bluetoothCommandManagement

import android.bluetooth.BluetoothDevice
import com.example.demoapp.ui.faultCodes.FaultCodesViewModel
import com.github.pires.obd.commands.protocol.ResetTroubleCodesCommand
import com.github.pires.obd.commands.protocol.TimeoutCommand
import java.io.InputStream
import java.io.OutputStream

/** This class is used to send the reset fault codes command within the Fault Codes page */
class ClearFaultsCommandSender(device: BluetoothDevice, providedViewModel: FaultCodesViewModel) :
    AbstractCommandSender<FaultCodesViewModel>(device, providedViewModel) {
    override fun performCommand(inputStream: InputStream, outputStream: OutputStream) {
        //Short timeout command to ensure the OBD device is finished with any previous commands
        val timeoutCommand = TimeoutCommand(62)
        timeoutCommand.run(inputStream, outputStream)

        val clearFaultsCommand = ResetTroubleCodesCommand()
        clearFaultsCommand.run(inputStream, outputStream)
    }
}
