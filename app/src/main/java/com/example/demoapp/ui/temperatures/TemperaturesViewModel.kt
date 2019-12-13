package com.example.demoapp.ui.temperatures

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TemperaturesViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is Temperatures Fragment"
    }
    val text: LiveData<String> = _text
}
