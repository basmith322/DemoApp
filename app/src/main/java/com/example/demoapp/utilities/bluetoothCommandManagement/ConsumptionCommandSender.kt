package com.example.demoapp.utilities.bluetoothCommandManagement

import android.bluetooth.BluetoothDevice
import com.example.demoapp.ui.consumption.ConsumptionViewModel
import com.github.pires.obd.commands.fuel.AirFuelRatioCommand
import com.github.pires.obd.commands.fuel.ConsumptionRateCommand
import com.github.pires.obd.commands.fuel.FuelLevelCommand
import com.github.pires.obd.commands.pressure.FuelPressureCommand
import com.github.pires.obd.commands.protocol.TimeoutCommand
import java.io.InputStream
import java.io.OutputStream

/** This class is used to send the relevant commands for the Consumption page and return the values
* to the consumption view model where the observer can see the changes*/
class ConsumptionCommandSender (device: BluetoothDevice, providedViewModel: ConsumptionViewModel) :
    AbstractCommandSender<ConsumptionViewModel>(device, providedViewModel) {

    override fun performCommand(inputStream: InputStream, outputStream: OutputStream) {
        //Short timeout command to ensure the OBD device is finished with any previous commands
        val timeoutCommand = TimeoutCommand(62)
        timeoutCommand.run(inputStream, outputStream)

        //Creating and running commands, storing the results in variables to be passed to view model
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

        //Setting up view model and posting the values to the relevant mutable properties.
        val cViewModel = viewModel
        cViewModel.currentConsumption.postValue(consumptionResult)
        cViewModel.currentAirFuelRatio.postValue(airFuelRatioResult)
        cViewModel.fuelLevel.postValue(fuelLevelResult)
        cViewModel.currentFuelPressure.postValue(pressureResult)
    }
}