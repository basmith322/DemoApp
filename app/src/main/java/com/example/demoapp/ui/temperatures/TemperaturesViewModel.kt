package com.example.demoapp.ui.temperatures

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TemperaturesViewModel : ViewModel() {

    //Current Coolant Temp Title
    private val _textCoolantTempTitle = MutableLiveData<String>().apply {
        value = "Coolant Temp"
    }
    val textCoolantTempTitle: LiveData<String> = _textCoolantTempTitle

    //Current coolant temp value returned from OBD
    val coolantTemp by lazy {
        MutableLiveData<String>()
    }


    //Current Air Intake Temp Title
    private val _textAirIntakeTempTitle = MutableLiveData<String>().apply {
        value = "Air Intake Temp"
    }
    val textAirIntakeTempTitle: LiveData<String> = _textAirIntakeTempTitle

    //Current Air Intake temp value returned from OBD
    val airIntakeTemp by lazy {
        MutableLiveData<String>()
    }


    //Current Ambient Air Temp Title
    private val _textAmbientAirTempTitle = MutableLiveData<String>().apply {
        value = "Ambient Air Temp"
    }
    val textAmbientAirTempTitle: LiveData<String> = _textAmbientAirTempTitle

    //Current Ambient Air Temp value returned from OBD
    val ambientAirTemp by lazy {
        MutableLiveData<String>()
    }


    //Current Temperature Title
    private val _textTemperatureTitle = MutableLiveData<String>().apply {
        value = "Temperature"
    }
    val textTemperatureTitle: LiveData<String> = _textTemperatureTitle

    //Current Temperature value returned from OBD
    val temperature by lazy {
        MutableLiveData<String>()
    }


    //Current Oil Temp title
    private val _textOilTempTitle = MutableLiveData<String>().apply {
        value = "Oil Temp"
    }
    val textOilTempTitle: LiveData<String> = _textOilTempTitle

    //Current Oil Temp value returned from OBD
    val oilTemp by lazy {
        MutableLiveData<String>()
    }
}
