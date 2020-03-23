package com.example.demoapp.utilities

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.ContentValues.TAG
import android.util.Log
import com.example.demoapp.ui.bluetooth.ConsumptionCommandSender
import com.example.demoapp.ui.bluetooth.PerformanceCommandSender
import com.example.demoapp.ui.consumption.ConsumptionViewModel
import com.example.demoapp.ui.performance.PerformanceViewModel
import com.github.pires.obd.commands.SpeedCommand
import com.github.pires.obd.commands.engine.RPMCommand
import com.github.pires.obd.commands.fuel.ConsumptionRateCommand
import com.github.pires.obd.commands.pressure.BarometricPressureCommand
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

var MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

class CommandService {

    fun connectToServerPerformance(performanceViewModel: PerformanceViewModel, device: BluetoothDevice) {
        PerformanceCommandSender(device, performanceViewModel).start()
    }

    fun connectToServerConsumption(consumptionViewModel: ConsumptionViewModel, device: BluetoothDevice) {
        ConsumptionCommandSender(device, consumptionViewModel).start()
    }
}