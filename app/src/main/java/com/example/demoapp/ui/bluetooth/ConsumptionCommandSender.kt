package com.example.demoapp.ui.bluetooth

import android.bluetooth.BluetoothDevice
import com.example.demoapp.ui.consumption.ConsumptionViewModel
import com.github.pires.obd.commands.fuel.AirFuelRatioCommand
import com.github.pires.obd.commands.fuel.ConsumptionRateCommand
import java.io.InputStream
import java.io.OutputStream

class ConsumptionCommandSender (device: BluetoothDevice, providedViewModel: ConsumptionViewModel) :
    AbstractCommandSender<ConsumptionViewModel>(device, providedViewModel) {

    override fun performCommand(inputStream: InputStream, outputStream: OutputStream) {
        val consumptionCommand = ConsumptionRateCommand()
        consumptionCommand.run(inputStream, outputStream)
        val consumptionResult = consumptionCommand.calculatedResult + "MPG"

        val airFuelRatioCommand = AirFuelRatioCommand()
        airFuelRatioCommand.run(inputStream,outputStream)
        val airFuelRatioResult = airFuelRatioCommand.formattedResult

        val cViewModel = viewModel
        cViewModel.currentConsumption.postValue(consumptionResult)
        cViewModel.currentAirFuelRatio.postValue(airFuelRatioResult)

    }
}