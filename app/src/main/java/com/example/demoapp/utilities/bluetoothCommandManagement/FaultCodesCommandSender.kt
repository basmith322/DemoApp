package com.example.demoapp.utilities.bluetoothCommandManagement

import android.bluetooth.BluetoothDevice
import com.example.demoapp.ui.faultCodes.FaultCodesViewModel
import com.github.pires.obd.commands.control.TroubleCodesCommand
import com.github.pires.obd.commands.protocol.TimeoutCommand
import java.io.InputStream
import java.io.OutputStream

/** This class is used to send the fault codes command from the fault codes page*/
class FaultCodesCommandSender(device: BluetoothDevice, providedViewModel: FaultCodesViewModel) :
    AbstractCommandSender<FaultCodesViewModel>(device, providedViewModel) {

    override fun performCommand(inputStream: InputStream, outputStream: OutputStream) {
        //Short timeout command to ensure the OBD device is finished with any previous commands
        val timeoutCommand = TimeoutCommand(255)
        timeoutCommand.run(inputStream, outputStream)

        val troubleCodesCommand = TroubleCodesCommand()
        troubleCodesCommand.run(inputStream, outputStream)

        /** Fault codes are returned as a String with newline break after each. The split allows each
         code to be added to as a new item in a list */
        var troubleCodesResult = troubleCodesCommand.formattedResult.split("\n")

        /**The split will find a newline at the end of the last fault code so it will make an array
        * of 4 items when there should only be 3. Last item is dropped as it will always be empty*/
        troubleCodesResult = troubleCodesResult.dropLast(1)

        //Result posted to fault code mutable property
        val fViewModel = viewModel
        fViewModel.faultCode.postValue(troubleCodesResult.toTypedArray())
    }
}