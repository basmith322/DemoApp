package com.example.demoapp.ui.bluetoothCommandManagement

import android.bluetooth.BluetoothDevice
import android.content.ContentValues
import android.util.Log
import com.example.demoapp.ui.temperatures.TemperaturesViewModel
import com.github.pires.obd.commands.engine.OilTempCommand
import com.github.pires.obd.commands.protocol.ObdResetCommand
import com.github.pires.obd.commands.temperature.AirIntakeTemperatureCommand
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand
import java.io.InputStream
import java.io.OutputStream

class TemperaturesCommandSender(device: BluetoothDevice, providedViewModel: TemperaturesViewModel) :
    AbstractCommandSender<TemperaturesViewModel>(device, providedViewModel) {

    override fun performCommand(inputStream: InputStream, outputStream: OutputStream) {
        val obdResetCommand = ObdResetCommand()
        obdResetCommand.run(inputStream, outputStream)

        try {
            sleep(500)
        } catch (e: InterruptedException) {
            Log.e(ContentValues.TAG,"Error with OBD reset:", e)
        }

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

        val tViewModel = viewModel
        tViewModel.coolantTemp.postValue(coolantTempResult)
        tViewModel.airIntakeTemp.postValue(airIntakeResult)
        tViewModel.ambientAirTemp.postValue(ambientAirTempResult)
        tViewModel.oilTemp.postValue(oilTempResult)
    }
}