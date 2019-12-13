package com.example.demoapp.ui.consumption

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ConsumptionViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is consumption Fragment"
    }
    val text: LiveData<String> = _text
}
