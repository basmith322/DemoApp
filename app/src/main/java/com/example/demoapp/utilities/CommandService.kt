package com.example.demoapp.utilities

import android.bluetooth.BluetoothDevice
import com.example.demoapp.ui.bluetoothCommandManagement.ConsumptionCommandSender
import com.example.demoapp.ui.bluetoothCommandManagement.PerformanceCommandSender
import com.example.demoapp.ui.bluetoothCommandManagement.TemperaturesCommandSender
import com.example.demoapp.ui.consumption.ConsumptionViewModel
import com.example.demoapp.ui.performance.PerformanceViewModel
import com.example.demoapp.ui.temperatures.TemperaturesViewModel
import java.util.*

var MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

class CommandService {

    fun connectToServerPerformance(performanceViewModel: PerformanceViewModel, device: BluetoothDevice) {
        PerformanceCommandSender(device, performanceViewModel).start()
    }

    fun connectToServerConsumption(consumptionViewModel: ConsumptionViewModel, device: BluetoothDevice) {
        ConsumptionCommandSender(device, consumptionViewModel).start()
    }

    fun connectToServerTemperature(temperaturesViewModel: TemperaturesViewModel, device: BluetoothDevice){
        TemperaturesCommandSender(device,temperaturesViewModel).start()
    }
}