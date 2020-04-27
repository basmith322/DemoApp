package com.example.demoapp.utilities.bluetoothCommandManagement

import android.bluetooth.BluetoothDevice
import com.example.demoapp.ui.temperatures.TemperaturesViewModel
import com.github.pires.obd.commands.engine.OilTempCommand
import com.github.pires.obd.commands.protocol.TimeoutCommand
import com.github.pires.obd.commands.temperature.AirIntakeTemperatureCommand
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand
import java.io.InputStream
import java.io.OutputStream

/** This class is used to send the relevant commands for the Performance page and return the values
* to the performance view model where the observer can see the changes*/
class TemperaturesCommandSender(device: BluetoothDevice, providedViewModel: TemperaturesViewModel) :
    AbstractCommandSender<TemperaturesViewModel>(device, providedViewModel) {

    override fun performCommand(inputStream: InputStream, outputStream: OutputStream) {
        //Short timeout command to ensure the OBD device is finished with any previous commands
        val timeoutCommand = TimeoutCommand(62)
        timeoutCommand.run(inputStream, outputStream)

        //Creating and running commands, storing the results in variables to be passed to view model
        val coolantTempCommand = EngineCoolantTemperatureCommand()
        coolantTempCommand.run(inputStream,outputStream)
        val coolantTempResult = coolantTempCommand.temperature

        val airIntakeTempCommand = AirIntakeTemperatureCommand()
        airIntakeTempCommand.run(inputStream,outputStream)
        val airIntakeResult = airIntakeTempCommand.temperature

        val ambientAirTempCommand = AmbientAirTemperatureCommand()
        ambientAirTempCommand.run(inputStream,outputStream)
        val ambientAirTempResult = ambientAirTempCommand.temperature

        val oilTempCommand = OilTempCommand()
        oilTempCommand.run(inputStream,outputStream)
        val oilTempResult = oilTempCommand.temperature

        //Setting up view model and posting the values to the relevant mutable properties.
        val tViewModel = viewModel
        tViewModel.coolantTemp.postValue(coolantTempResult)
        tViewModel.airIntakeTemp.postValue(airIntakeResult)
        tViewModel.ambientAirTemp.postValue(ambientAirTempResult)
        tViewModel.oilTemp.postValue(oilTempResult)
    }
}