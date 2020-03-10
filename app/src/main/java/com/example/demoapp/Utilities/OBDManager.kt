package com.example.demoapp.utilities

interface OBDManager {
    fun getCurrentRpm(): Int
    fun getCoolantTemperature() : Float
    fun getIntakeTemperature() : Float
    fun getKnockRetard() : Int
    fun getPidValue(pid:String): Int
}
