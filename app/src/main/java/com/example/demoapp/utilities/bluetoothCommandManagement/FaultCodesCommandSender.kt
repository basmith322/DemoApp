package com.example.demoapp.utilities.bluetoothCommandManagement

import android.bluetooth.BluetoothDevice
import br.ufrn.imd.obd.commands.control.TroubleCodesCommand
import br.ufrn.imd.obd.commands.protocol.TimeoutCommand
import com.example.demoapp.ui.faultCodes.FaultCodesViewModel
import java.io.InputStream
import java.io.OutputStream

class FaultCodesCommandSender(device: BluetoothDevice, providedViewModel: FaultCodesViewModel) :
    AbstractCommandSender<FaultCodesViewModel>(device, providedViewModel) {

    override fun performCommand(inputStream: InputStream, outputStream: OutputStream) {
        val timeoutCommand = TimeoutCommand(62)
        timeoutCommand.run(inputStream,outputStream)

        val troubleCodesCommand = TroubleCodesCommand()
        troubleCodesCommand.run(inputStream,outputStream)
        var troubleCodesResult = troubleCodesCommand.formattedResult.split("\n")
        troubleCodesResult = troubleCodesResult.dropLast(1)
        val fViewModel = viewModel
      fViewModel.faultCode.postValue(troubleCodesResult.toTypedArray())
    }
}