package com.example.demoapp.ui.bluetooth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BluetoothSettingsViewModel : ViewModel() {
    val returnedProtocol by lazy {
        MutableLiveData<String>()
    }
}
