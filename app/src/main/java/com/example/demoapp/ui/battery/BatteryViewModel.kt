package com.example.demoapp.ui.battery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BatteryViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is battery Fragment"
    }
    val text: LiveData<String> = _text
}