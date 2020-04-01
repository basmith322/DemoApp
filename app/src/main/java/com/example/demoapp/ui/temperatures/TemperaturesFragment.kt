package com.example.demoapp.ui.temperatures

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
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
import com.example.demoapp.R
import com.example.demoapp.utilities.CommandService
import kotlinx.android.synthetic.main.fragment_temperatures.*

class TemperaturesFragment : Fragment() {

    private val temperaturesViewModel: TemperaturesViewModel by viewModels()
    lateinit var mainHandler: Handler
    private lateinit var data: Bundle
    private lateinit var currentDevice: BluetoothDevice
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

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

        //Air Intake Temp value returned from OBD
        val airIntakeTempObserver = Observer<String> { airIntakeTempFromOBD ->
            textView_AirIntakeTemp.text = airIntakeTempFromOBD
        }
        temperaturesViewModel.airIntakeTemp.observe(viewLifecycleOwner, airIntakeTempObserver)


        //Ambient Air Temp Title
        val textAmbientAirTempTitle: TextView = root.findViewById(R.id.textView_AmbientAirTempTitle)
        temperaturesViewModel.textAmbientAirTempTitle.observe(viewLifecycleOwner, Observer {
            textAmbientAirTempTitle.text = it
        })

        //Ambient Air Temp value returned from OBD
        val ambientAirTempObserver = Observer<String> { ambientAirTempFromOBD ->
            textView_AmbientAirTemp.text = ambientAirTempFromOBD
        }
        temperaturesViewModel.ambientAirTemp.observe(viewLifecycleOwner, ambientAirTempObserver)


        //Oil Temp Title
        val textOilTempTitle: TextView = root.findViewById(R.id.textView_OilTempTitle)
        temperaturesViewModel.textOilTempTitle.observe(viewLifecycleOwner, Observer {
            textOilTempTitle.text = it
        })

        //Oil Temp value returned from OBD
        val oilTempObserver = Observer<String> { currentOilTempReturnedFromOBD ->
            textView_OilTemp.text = currentOilTempReturnedFromOBD
        }
        temperaturesViewModel.oilTemp.observe(viewLifecycleOwner, oilTempObserver)

        return root
    }

    private val updateTemperaturesTask = object : Runnable {
        override fun run() {
            if (bluetoothAdapter?.isEnabled == true) {
                try {
                    data = arguments!!
                    currentDevice = data.get("currentDevice") as BluetoothDevice
                } catch (e: Exception) {
                    Log.e(TAG, "Device not yet set, Falling back to default device", e)
                    try {
                        val pairedDevices = bluetoothAdapter.bondedDevices
                        currentDevice = pairedDevices!!.first()
                    } catch (e: Exception) {
                        Log.e(TAG, "No devices in device list")
                    }
                }
                try {
                    CommandService().connectToServerTemperature(temperaturesViewModel, currentDevice)
                } catch (e: Exception) {
                    Log.e(TAG, "Error Connecting to Server: ", e)
                }
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
