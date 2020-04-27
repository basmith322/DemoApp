package com.example.demoapp.ui.temperatures

import android.content.ContentValues.TAG
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.cardiomood.android.controls.gauge.SpeedometerGauge
import com.example.demoapp.R
import com.example.demoapp.utilities.CommandService
import com.example.demoapp.utilities.DeviceSingleton
import kotlinx.android.synthetic.main.fragment_temperatures.*
import kotlin.math.roundToInt

class TemperaturesFragment : Fragment() {

    private val temperaturesViewModel: TemperaturesViewModel by viewModels()
    lateinit var mainHandler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainHandler = Handler(Looper.getMainLooper())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_temperatures, container, false)

        val coolantGauge: SpeedometerGauge = root.findViewById(R.id.coolantTemp)

        coolantGauge.labelConverter =
            SpeedometerGauge.LabelConverter { progress, maxProgress -> (progress.roundToInt()).toString() }
        coolantGauge.maxSpeed = 130.0
        coolantGauge.majorTickStep = 16.0
        coolantGauge.addColoredRange(120.0, 130.0, Color.RED)

        val oilTempGauge: SpeedometerGauge = root.findViewById(R.id.oilTemp)

        oilTempGauge.labelConverter =
            SpeedometerGauge.LabelConverter { progress, maxProgress -> (progress.roundToInt()).toString() }
        oilTempGauge.maxSpeed = 150.0
        oilTempGauge.majorTickStep = 20.0
        oilTempGauge.addColoredRange(120.0, 150.0, Color.RED)

        val ambientAirTempGauge: SpeedometerGauge = root.findViewById(R.id.ambientAirTemp)
        ambientAirTempGauge.labelConverter =
            SpeedometerGauge.LabelConverter { progress, maxProgress -> (progress.roundToInt()).toString() }
        ambientAirTempGauge.maxSpeed = 60.0
        ambientAirTempGauge.majorTickStep = 5.0
        ambientAirTempGauge.addColoredRange(0.0, 10.0, Color.BLUE)
        ambientAirTempGauge.addColoredRange(10.0, 18.0, Color.CYAN)
        ambientAirTempGauge.addColoredRange(18.0, 30.0, Color.YELLOW)
        ambientAirTempGauge.addColoredRange(30.0, 60.0, Color.RED)

        val airIntakeTempGauge: SpeedometerGauge = root.findViewById(R.id.airIntakeTemp)
        airIntakeTempGauge.labelConverter =
            SpeedometerGauge.LabelConverter{progress, maxProgress -> (progress.roundToInt()).toString()  }
        airIntakeTempGauge.maxSpeed = 150.0
        airIntakeTempGauge.majorTickStep = 15.0
        airIntakeTempGauge.addColoredRange(120.0, 150.0, Color.RED)

        //Current Coolant Temp Title
        val textCoolantTempTitle: TextView =
            root.findViewById(R.id.textView_CoolantTempTitle)
        temperaturesViewModel.textCoolantTempTitle.observe(viewLifecycleOwner, Observer {
            textCoolantTempTitle.text = it
        })

        //Current Coolant Temp value returned from OBD
        val coolantObserver = Observer<Float> { currentCoolantTempFromOBD ->
            val coolantOutput = "$currentCoolantTempFromOBD C"
            textView_CoolantTemp.text = coolantOutput
            coolantGauge.speed = currentCoolantTempFromOBD.toDouble()
        }
        temperaturesViewModel.coolantTemp.observe(viewLifecycleOwner, coolantObserver)


        //Air Intake Temp Title
        val textAirIntakeTempTitle: TextView = root.findViewById(R.id.textView_AirIntakeTempTitle)
        temperaturesViewModel.textAirIntakeTempTitle.observe(viewLifecycleOwner, Observer {
            textAirIntakeTempTitle.text = it
        })

        //Air Intake Temp value returned from OBD
        val airIntakeTempObserver = Observer<Float> { airIntakeTempFromOBD ->
            val airIntakeOutput = "$airIntakeTempFromOBD C"
            textView_AirIntakeTemp.text = airIntakeOutput
            airIntakeTemp.speed = airIntakeTempFromOBD.toDouble()
        }
        temperaturesViewModel.airIntakeTemp.observe(viewLifecycleOwner, airIntakeTempObserver)


        //Ambient Air Temp Title
        val textAmbientAirTempTitle: TextView = root.findViewById(R.id.textView_AmbientAirTempTitle)
        temperaturesViewModel.textAmbientAirTempTitle.observe(viewLifecycleOwner, Observer {
            textAmbientAirTempTitle.text = it
        })

        //Ambient Air Temp value returned from OBD
        val ambientAirTempObserver = Observer<Float> { ambientAirTempFromOBD ->
            val ambientTempOutput = "$ambientAirTempFromOBD C"
            textView_AmbientAirTemp.text = ambientTempOutput
            ambientAirTemp.speed = ambientAirTempFromOBD.toDouble()
        }
        temperaturesViewModel.ambientAirTemp.observe(viewLifecycleOwner, ambientAirTempObserver)


        //Oil Temp Title
        val textOilTempTitle: TextView = root.findViewById(R.id.textView_OilTempTitle)
        temperaturesViewModel.textOilTempTitle.observe(viewLifecycleOwner, Observer {
            textOilTempTitle.text = it
        })

        //Oil Temp value returned from OBD
        val oilTempObserver = Observer<Float> { currentOilTempReturnedFromOBD ->
            val oilTempOutput = "$currentOilTempReturnedFromOBD C"
            textView_OilTemp.text = oilTempOutput
            oilTemp.speed = currentOilTempReturnedFromOBD.toDouble()
        }
        temperaturesViewModel.oilTemp.observe(viewLifecycleOwner, oilTempObserver)

        return root
    }


    private val updateTemperaturesTask = object : Runnable {
        override fun run() {
            try {
                CommandService().connectToServerTemperature(temperaturesViewModel, DeviceSingleton.bluetoothDevice!!)
            } catch (e: Exception) {
                Log.e(TAG, "Error Connecting to Server: ", e)
            }
            mainHandler.postDelayed(this, 2000)
        }
    }

    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(updateTemperaturesTask)
    }

    override fun onResume() {
        super.onResume()
        mainHandler.post(updateTemperaturesTask)
    }
}
