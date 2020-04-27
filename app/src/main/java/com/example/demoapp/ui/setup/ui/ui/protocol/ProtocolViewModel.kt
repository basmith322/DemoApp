package com.example.demoapp.ui.setup.ui.ui.protocol

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.pires.obd.enums.ObdProtocols

class ProtocolViewModel : ViewModel() {
    //mutable property to hold the returned protocol from the OBD system
    val returnedProtocol by lazy {
        MutableLiveData<String>()
    }
    var hasConnected: Boolean = false
    var odbProtocol: ObdProtocols = ObdProtocols.AUTO
}
