package com.example.demoapp.ui.consumption

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ConsumptionViewModel : ViewModel() {
    private val _textCurrentConsumptionTitle = MutableLiveData<String>().apply {
        value = "Current Consumption"
    }
    val textCurrentSpeedTitle: LiveData<String> = _textCurrentConsumptionTitle

    val currentConsumption by lazy {
        MutableLiveData<String>()
    }

    private val _textAvgConsumptionTitle = MutableLiveData<String>().apply {
        value = "Avg Consumption"
    }
    val textAvgConsumptionTitle: LiveData<String> = _textAvgConsumptionTitle

    val avgConsumption by lazy {
        MutableLiveData<String>()
    }

    private val _textRangeTitle = MutableLiveData<String>().apply {
        value = "Fuel Level"
    }
    val textRangeTitle: LiveData<String> = _textRangeTitle

    val fuelLevel by lazy {
        MutableLiveData<Float>()
    }

    private val _textFuelPressureTitle = MutableLiveData<String>().apply {
        value = "Fuel Pressure"
    }
    val textFuelPressureTitle: LiveData<String> = _textFuelPressureTitle

    val currentFuelPressure by lazy {
        MutableLiveData<Int>()
    }

    private val _textAirFuelRatioTitle = MutableLiveData<String>().apply {
        value = "Air/Fuel Ratio"
    }
    val textAirFuelRatioTitle: LiveData<String> = _textAirFuelRatioTitle

    val currentAirFuelRatio by lazy {
        MutableLiveData<String>()
    }

//    private val _textFuelPressureTitle = MutableLiveData<String>().apply {
//        value = "Fuel Pressure"
//    }
//    val textFuelPressureTitle: LiveData<String> = _textFuelPressureTitle
//
//    val currentFuelPressure by lazy {
//        MutableLiveData<String>()
//    }

}
