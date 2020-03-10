package com.example.demoapp.utilities

import android.bluetooth.BluetoothAdapter
import android.content.Context
import com.github.pires.obd.commands.engine.RPMCommand
import com.github.pires.obd.commands.temperature.AirIntakeTemperatureCommand
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class BluetoothODBManager: OBDManager {
    val context: Context
    val input: InputStream
    val output: OutputStream
    val rpmCommand: RPMCommand
    val coolantTempCommand: EngineCoolantTemperatureCommand
    val airIntakeTempCommand: AirIntakeTemperatureCommand

    constructor(context: Context) {
        this.context = context

        val adapter = BluetoothAdapter.getDefaultAdapter();
        val device = adapter.bondedDevices.iterator().next()
        var socket = device.createRfcommSocketToServiceRecord(UUID.randomUUID())
        socket.connect()

        input = socket.inputStream
        output = socket.outputStream
        rpmCommand = RPMCommand()
        coolantTempCommand = EngineCoolantTemperatureCommand()
        airIntakeTempCommand = AirIntakeTemperatureCommand()
    }

    override fun getCoolantTemperature(): Float {
        coolantTempCommand.run(input, output)
        return coolantTempCommand.temperature
    }

    override fun getIntakeTemperature(): Float {
        airIntakeTempCommand.run(input, output)
        return airIntakeTempCommand.temperature
    }

    override fun getKnockRetard(): Int {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCurrentRpm(): Int {
        rpmCommand.run(input, output)
        return rpmCommand.rpm
    }

    override fun getPidValue(pid: String): Int {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}