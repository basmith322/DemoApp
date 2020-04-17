package com.example.demoapp.ui.faultCodes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FaultCodesViewModel : ViewModel() {
    val faultCode by lazy {
        MutableLiveData<Array<String>>()
    }

    val returnedProtocol by lazy {
        MutableLiveData<String>()
    }
}
