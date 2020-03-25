package com.example.demoapp.ui.bluetooth

import android.bluetooth.BluetoothDevice
import com.example.demoapp.ui.temperatures.TemperaturesViewModel
import com.github.pires.obd.commands.engine.OilTempCommand
import com.github.pires.obd.commands.temperature.AirIntakeTemperatureCommand
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand
import java.io.InputStream
import java.io.OutputStream

class TemperaturesCommandSender(device: BluetoothDevice, providedViewModel: TemperaturesViewModel) :
    AbstractCommandSender<TemperaturesViewModel>(device, providedViewModel) {

    override fun performCommand(inputStream: InputStream, outputStream: OutputStream) {
        val coolantTempCommand = EngineCoolantTemperatureCommand()
        coolantTempCommand.run(inputStream,outputStream)
        val coolantTempResult = coolantTempCommand.calculatedResult + "C"

        val airIntakeTempCommand = AirIntakeTemperatureCommand()
        airIntakeTempCommand.run(inputStream,outputStream)
        val airIntakeResult = airIntakeTempCommand.calculatedResult + "C"

        val ambientAirTempCommand = AmbientAirTemperatureCommand()
        ambientAirTempCommand.run(inputStream,outputStream)
        val ambientAirTempResult = ambientAirTempCommand.calculatedResult + "C"

        val oilTempCommand = OilTempCommand()
        oilTempCommand.run(inputStream,outputStream)
        val oilTempResult = oilTempCommand.calculatedResult + "C"

        val tViewModel = viewModel
        tViewModel.coolantTemp.postValue(coolantTempResult)
        tViewModel.airIntakeTemp.postValue(airIntakeResult)
        tViewModel.ambientAirTemp.postValue(ambientAirTempResult)
        tViewModel.oilTemp.postValue(oilTempResult)
    }
}