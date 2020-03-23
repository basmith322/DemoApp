package com.example.demoapp.ui.bluetooth

import android.bluetooth.BluetoothDevice
import com.example.demoapp.ui.temperatures.TemperaturesViewModel
import java.io.InputStream
import java.io.OutputStream

class TemperaturesCommandSender(device: BluetoothDevice, providedViewModel: TemperaturesViewModel) :
    AbstractCommandSender<TemperaturesViewModel>(device, providedViewModel) {

    override fun performCommand(inputStream: InputStream, outputStream: OutputStream) {
    }
}