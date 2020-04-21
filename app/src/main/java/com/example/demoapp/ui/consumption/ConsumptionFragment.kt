package com.example.demoapp.ui.consumption

import android.content.ContentValues.TAG
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
import kotlinx.android.synthetic.main.fragment_consumption.*
import kotlin.math.roundToInt

class ConsumptionFragment : Fragment() {
    private val consumptionViewModel: ConsumptionViewModel by viewModels()
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
        val root = inflater.inflate(R.layout.fragment_consumption, container, false)

        val fuelGauge: SpeedometerGauge
        fuelGauge = root.findViewById(R.id.fuelLevel)
        fuelGauge.labelConverter =
            SpeedometerGauge.LabelConverter{progress, maxProgress -> progress.roundToInt().toString() }
        fuelGauge.maxSpeed = 100.0
        fuelGauge.majorTickStep = 20.0


        //Current Consumption Title
        val textConsumptionTitle: TextView =
            root.findViewById(R.id.textView_CurrentConsumptionTitle)
        consumptionViewModel.textCurrentSpeedTitle.observe(viewLifecycleOwner, Observer {
            textConsumptionTitle.text = it
        })

        //Current Consumption value returned from OBD
        val consumptionObserver = Observer<String> { currentConsumptionFromODB ->
            textView_CurrentConsumption.text = currentConsumptionFromODB
        }
        consumptionViewModel.currentConsumption.observe(viewLifecycleOwner, consumptionObserver)

        //Fuel Level Title
        val textFuelLevelTitle: TextView = root.findViewById(R.id.textView_FuelLevelTitle)
        consumptionViewModel.textRangeTitle.observe(viewLifecycleOwner, Observer {
            textFuelLevelTitle.text = it
        })

        //Current Fuel Level value returned from OBD
        val fuelLevelObserver = Observer<Float> { currentFuelLevelFromOBD ->
            val fuelOutput = currentFuelLevelFromOBD.toInt().toString() + "%"
            textView_FuelLevel.text = fuelOutput
            fuelGauge.speed = currentFuelLevelFromOBD.toDouble()
        }
        consumptionViewModel.fuelLevel.observe(viewLifecycleOwner, fuelLevelObserver)


        //Fuel Pressure Title
        val textFuelPressureTitle: TextView = root.findViewById(R.id.textView_FuelPressureTitle)
        consumptionViewModel.textFuelPressureTitle.observe(viewLifecycleOwner, Observer {
            textFuelPressureTitle.text = it
        })

        //Current Fuel pressure value returned from OBD
        val fuelPressureObserver = Observer<String> { currentFuelPressureFromOBD ->
            textView_FuelPressure.text = currentFuelPressureFromOBD
        }
        consumptionViewModel.currentFuelPressure.observe(viewLifecycleOwner, fuelPressureObserver)

        //Air/Fuel Ratio Title
        val textAirFuelRatioTitle: TextView = root.findViewById(R.id.textView_AirFuelRatioTitle)
        consumptionViewModel.textAirFuelRatioTitle.observe(viewLifecycleOwner, Observer {
            textAirFuelRatioTitle.text = it
        })

        //Current Air/Fuel ratio value returned from OBD
        val airFuelObserver = Observer<String> { currentAirFuelFromOBD ->
            textView_AirFuelRatio.text = currentAirFuelFromOBD
        }
        consumptionViewModel.currentAirFuelRatio.observe(viewLifecycleOwner, airFuelObserver)

        return root
    }

    private val updateConsumptionTask = object : Runnable {
        override fun run() {
            try {
                CommandService().connectToServerConsumption(consumptionViewModel, DeviceSingleton.bluetoothDevice!!)
            } catch (e: Exception) {
                Log.e(TAG, "Error Connecting to Server: ", e)
            }
            mainHandler.postDelayed(this, 2000)
        }
    }

    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(updateConsumptionTask)
    }

    override fun onResume() {
        super.onResume()
        mainHandler.post(updateConsumptionTask)
    }
}