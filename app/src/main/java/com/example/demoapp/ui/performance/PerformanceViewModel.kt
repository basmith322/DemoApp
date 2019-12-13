package com.example.demoapp.ui.performance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PerformanceViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is performance Fragment"
    }
    val text: LiveData<String> = _text
}