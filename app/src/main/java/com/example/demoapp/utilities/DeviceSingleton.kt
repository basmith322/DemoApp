package com.example.demoapp.utilities

import android.bluetooth.BluetoothDevice
/** Singleton object is used to store the value of the Bluetooth Device so it can be used
 * throughout all fragments and activities where necessary*/
object DeviceSingleton{
    var bluetoothDevice: BluetoothDevice? = null
}