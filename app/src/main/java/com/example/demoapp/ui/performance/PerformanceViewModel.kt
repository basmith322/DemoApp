package com.example.demoapp.ui.performance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PerformanceViewModel : ViewModel() {

    private val randomMph = (0..130).random()
    private val randomRPM = (600..6000).random()
    private val randomPSI = (0..300).random()
    private val randomMaxSpeed = (60..200).random()

    //Current Speed
    private val _textCurrentSpeedTitle = MutableLiveData<String>().apply {
        value = "Current Speed"
    }
    val textCurrentSpeedTitle: LiveData<String> = _textCurrentSpeedTitle

    private val _textCurrentSpeed = MutableLiveData<Int>().apply {
        value = randomMph
    }
    val textCurrentSpeed: LiveData<Int> = _textCurrentSpeed

    //RPM
    private val _textRPMTitle = MutableLiveData<String>().apply {
        value = "Current RPM"
    }
    val textRPMTitle: LiveData<String> = _textRPMTitle

    private val _textRPM = MutableLiveData<Int>().apply {
        value = randomRPM
    }
    val textRPM: LiveData<Int> = _textRPM

    //Boost Pressure
    private val _textPSITitle = MutableLiveData<String>().apply {
        value = "Current PSI"
    }
    val textPSITitle: LiveData<String> = _textPSITitle

    private val _textPSI = MutableLiveData<Int>().apply {
        value = randomPSI
    }
    val textPSI: LiveData<Int> = _textPSI

    //Avg Speed
    private val _textAvgSpeedTitle = MutableLiveData<String>().apply {
        value = "Average Speed"
    }
    val textAvgSpeedTitle: LiveData<String> = _textAvgSpeedTitle

    private val _textAvgSpeed = MutableLiveData<Int>().apply {
        value = randomMph
    }
    val textAvgSpeed: LiveData<Int> = _textAvgSpeed

    //Max Speed
    private val _textMaxSpeedTitle = MutableLiveData<String>().apply {
        value = "Max Speed"
    }
    val textMaxSpeedTitle: LiveData<String> = _textMaxSpeedTitle

    private val _textMaxSpeed = MutableLiveData<Int>().apply {
        value = randomMaxSpeed
    }
    val textMaxSpeed: LiveData<Int> = _textMaxSpeed

}