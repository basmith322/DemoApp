package com.example.demoapp.ui.temperatures

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TemperaturesViewModel : ViewModel() {
    /**Mutable properties that can be changed. Titles are static but values are set to returned
     * values from OBD commands */
    //Current Coolant Temp Title
    private val _textCoolantTempTitle = MutableLiveData<String>().apply {
        value = "Coolant Temp"
    }
    val textCoolantTempTitle: LiveData<String> = _textCoolantTempTitle

    //Current coolant temp value returned from OBD
    val coolantTemp by lazy {
        MutableLiveData<Float>()
    }

    //Current Air Intake Temp Title
    private val _textAirIntakeTempTitle = MutableLiveData<String>().apply {
        value = "Air Intake Temp"
    }
    val textAirIntakeTempTitle: LiveData<String> = _textAirIntakeTempTitle

    //Current Air Intake temp value returned from OBD
    val airIntakeTemp by lazy {
        MutableLiveData<Float>()
    }

    //Current Ambient Air Temp Title
    private val _textAmbientAirTempTitle = MutableLiveData<String>().apply {
        value = "Ambient Air Temp"
    }
    val textAmbientAirTempTitle: LiveData<String> = _textAmbientAirTempTitle

    //Current Ambient Air Temp value returned from OBD
    val ambientAirTemp by lazy {
        MutableLiveData<Float>()
    }

    //Current Oil Temp title
    private val _textOilTempTitle = MutableLiveData<String>().apply {
        value = "Oil Temp"
    }
    val textOilTempTitle: LiveData<String> = _textOilTempTitle

    //Current Oil Temp value returned from OBD
    val oilTemp by lazy {
        MutableLiveData<Float>()
    }
}
