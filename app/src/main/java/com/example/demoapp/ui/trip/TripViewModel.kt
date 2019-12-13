package com.example.demoapp.ui.trip

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TripViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is trip Fragment"
    }
    val text: LiveData<String> = _text
}