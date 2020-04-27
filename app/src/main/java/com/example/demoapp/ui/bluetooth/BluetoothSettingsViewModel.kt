package com.example.demoapp.ui.bluetooth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BluetoothSettingsViewModel : ViewModel() {
    //Mutable property that can be changed based on value returned from OBD
    val returnedProtocol by lazy {
        MutableLiveData<String>()
    }
    //Used to determine if the service has connected or not.
    var hasConnected: Boolean = false
}
