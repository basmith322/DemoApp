package com.example.demoapp.ui.faultCodes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FaultCodesViewModel : ViewModel() {
    //Mutable property to hold the array returned from the fault codes command
    val faultCode by lazy {
        MutableLiveData<Array<String>>()
    }

}
