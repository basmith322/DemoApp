package com.example.demoapp.utilities.bluetoothCommandManagement

import android.bluetooth.BluetoothDevice
import br.ufrn.imd.obd.commands.fuel.AirFuelRatioCommand
import br.ufrn.imd.obd.commands.fuel.ConsumptionRateCommand
import br.ufrn.imd.obd.commands.fuel.FuelLevelCommand
import br.ufrn.imd.obd.commands.pressure.FuelPressureCommand
import com.example.demoapp.ui.consumption.ConsumptionViewModel
import java.io.InputStream
import java.io.OutputStream

class ConsumptionCommandSender (device: BluetoothDevice, providedViewModel: ConsumptionViewModel) :
    AbstractCommandSender<ConsumptionViewModel>(device, providedViewModel) {

    override fun performCommand(inputStream: InputStream, outputStream: OutputStream) {
        val consumptionCommand = ConsumptionRateCommand()
        consumptionCommand.run(inputStream, outputStream)
        val consumptionResult = consumptionCommand.formattedResult

        val airFuelRatioCommand = AirFuelRatioCommand()
        airFuelRatioCommand.run(inputStream,outputStream)
        val airFuelRatioResult = airFuelRatioCommand.formattedResult

        val pressureCommand = FuelPressureCommand()
        pressureCommand.run(inputStream,outputStream)
        val pressureResult = pressureCommand.formattedResult

        val fuelLevelCommand = FuelLevelCommand()
        fuelLevelCommand.run(inputStream,outputStream)
        val fuelLevelResult = fuelLevelCommand.fuelLevel

        val cViewModel = viewModel
        cViewModel.currentConsumption.postValue(consumptionResult)
        cViewModel.currentAirFuelRatio.postValue(airFuelRatioResult)
        cViewModel.fuelLevel.postValue(fuelLevelResult)
        cViewModel.currentFuelPressure.postValue(pressureResult)

    }
}