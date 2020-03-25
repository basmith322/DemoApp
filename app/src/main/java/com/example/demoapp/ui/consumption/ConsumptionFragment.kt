package com.example.demoapp.ui.consumption

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.demoapp.R
import com.example.demoapp.ui.bluetooth.REQUEST_ENABLE_BT
import com.example.demoapp.utilities.CommandService
import kotlinx.android.synthetic.main.fragment_consumption.*

class ConsumptionFragment : Fragment() {
    private val consumptionViewModel: ConsumptionViewModel by viewModels()
    lateinit var mainHandler: Handler
    private lateinit var data: Bundle
    private lateinit var currentDevice: BluetoothDevice
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainHandler = Handler(Looper.getMainLooper())

        if (bluetoothAdapter == null) {
            Toast.makeText(context, "This device does not support bluetooth", Toast.LENGTH_LONG)
                .show()
        }

        //if the device supports bluetooth but adapter is not enabled, request it to be enabled
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(
                enableBtIntent,
                REQUEST_ENABLE_BT
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_consumption, container, false)

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


        //Avg Consumption Title
        val textAvgConsumptionTitle: TextView = root.findViewById(R.id.textView_AvgConsumptionTitle)
        consumptionViewModel.textAvgConsumptionTitle.observe(viewLifecycleOwner, Observer {
            textAvgConsumptionTitle.text = it
        })

        //Avg Consumption value returned from OBD
        val avgConsumptionObserver = Observer<String> { avgConsumptionFromOBD ->
            textView_AvgConsumption.text = avgConsumptionFromOBD
        }
        consumptionViewModel.avgConsumption.observe(viewLifecycleOwner, avgConsumptionObserver)


        //Fuel Range Title
        val textRangeTitle: TextView = root.findViewById(R.id.textView_RangeTitle)
        consumptionViewModel.textRangeTitle.observe(viewLifecycleOwner, Observer {
            textRangeTitle.text = it
        })

        //Current Range value returned from OBD
        val rangeObserver = Observer<String> { currentRangeFromOBD ->
            textView_Range.text = currentRangeFromOBD
        }
        consumptionViewModel.currentRange.observe(viewLifecycleOwner, rangeObserver)


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
            if (bluetoothAdapter?.isEnabled == true) {
                try {
                    data = arguments!!
                    currentDevice = data.get("currentDevice") as BluetoothDevice
                } catch (e: Exception) {
                    Log.e(TAG,"Device not yet set, Falling back to default device", e)
                    try {
                        val pairedDevices = bluetoothAdapter.bondedDevices
                        currentDevice = pairedDevices!!.first()
                    } catch (e: Exception) {
                        Log.e(TAG, "No devices in device list")
                    }
                }
                try {
                    CommandService().connectToServerConsumption(consumptionViewModel, currentDevice)
                } catch (e: Exception) {
                    Log.e(TAG, "Error Connecting to Server: ", e)
                }
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