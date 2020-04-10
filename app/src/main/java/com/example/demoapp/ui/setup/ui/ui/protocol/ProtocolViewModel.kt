package com.example.demoapp.ui.setup.ui.ui.protocol

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProtocolViewModel : ViewModel() {
    val returnedProtocol by lazy {
        MutableLiveData<String>()
    }
}
