package com.example.demoapp.ui.temperatures

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.demoapp.R
import kotlinx.android.synthetic.main.fragment_consumption.*
import kotlinx.android.synthetic.main.fragment_temperatures.*

class TemperaturesFragment : Fragment() {

    private val temperaturesViewModel: TemperaturesViewModel by viewModels()
    lateinit var mainHandler: Handler
    private lateinit var data: Bundle
    private lateinit var currentDevice: BluetoothDevice
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

       val root = inflater.inflate(R.layout.fragment_temperatures, container, false)

        //Current Coolant Temp Title
        val textCoolantTempTitle: TextView =
            root.findViewById(R.id.textView_CoolantTempTitle)
        temperaturesViewModel.textCoolantTempTitle.observe(viewLifecycleOwner, Observer {
            textCoolantTempTitle.text = it
        })

        //Current Coolant Temp value returned from OBD
        val coolantObserver = Observer<String> { currentCoolantTempFromOBD ->
            textView_CoolantTemp.text = currentCoolantTempFromOBD
        }
        temperaturesViewModel.coolantTemp.observe(viewLifecycleOwner, coolantObserver)


        //Air Intake Temp Title
        val textAirIntakeTempTitle: TextView = root.findViewById(R.id.textView_AirIntakeTempTitle)
        temperaturesViewModel.textAirIntakeTempTitle.observe(viewLifecycleOwner, Observer {
            textAirIntakeTempTitle.text = it
        })

        //Avg Consumption value returned from OBD
        val airIntakeTempObserver = Observer<String> { airIntakeTempFromOBD ->
            textView_AirIntakeTemp.text = airIntakeTempFromOBD
        }
        temperaturesViewModel.airIntakeTemp.observe(viewLifecycleOwner, airIntakeTempObserver)


        //Ambient Air Temp Title
        val textAmbientAirTempTitle: TextView = root.findViewById(R.id.textView_AmbientAirTempTitle)
        temperaturesViewModel.textAmbientAirTempTitle.observe(viewLifecycleOwner, Observer {
            textAmbientAirTempTitle.text = it
        })

        //Current Range value returned from OBD
        val ambientAirTempObserver = Observer<String> { currentAirTempFromOBD ->
            textAmbientAirTempTitle.text = currentAirTempFromOBD
        }
        temperaturesViewModel.ambientAirTemp.observe(viewLifecycleOwner, ambientAirTempObserver)


        //Temperature Title
        val textTempTitle: TextView = root.findViewById(R.id.textView_TempTitle)
        temperaturesViewModel.textTemperatureTitle.observe(viewLifecycleOwner, Observer {
            textTempTitle.text = it
        })

        //Temp value returned from OBD
        val tempObserver = Observer<String> { currentTempReturnedFromOBD ->
            textView_Temp.text = currentTempReturnedFromOBD
        }
        temperaturesViewModel.temperature.observe(viewLifecycleOwner, tempObserver)

        return root
    }

}
