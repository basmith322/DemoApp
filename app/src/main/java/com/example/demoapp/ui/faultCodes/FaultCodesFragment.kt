package com.example.demoapp.ui.faultCodes

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.ContentValues
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.demoapp.R
import com.example.demoapp.utilities.CommandService
import kotlinx.android.synthetic.main.fault_codes_fragment.*

class FaultCodesFragment : Fragment() {
    private val faultCodesViewModel: FaultCodesViewModel by viewModels()
    lateinit var mainHandler: Handler
    private lateinit var data: Bundle
    private lateinit var currentDevice: BluetoothDevice
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainHandler = Handler(Looper.getMainLooper())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fault_codes_fragment, container, false)

        //fault code returned from OBD
        val faultObserver = Observer<String> { currentFaultFromOBD ->
            // Update the UI, in this case, a TextView.
            textView_currentFault.text = currentFaultFromOBD
        }
        faultCodesViewModel.faultCode.observe(viewLifecycleOwner, faultObserver)

        return root
    }

    private val updateFaultsTask = object : Runnable {
        override fun run() {
            if (bluetoothAdapter?.isEnabled == true) {
                try {
                    data = arguments!!
                    currentDevice = data.get("currentDevice") as BluetoothDevice
                } catch (e: Exception) {
                    Log.e(
                        ContentValues.TAG,
                        "Device not yet set, Falling back to default device",
                        e
                    )
                    try {
                        val pairedDevices = bluetoothAdapter.bondedDevices
                        currentDevice = pairedDevices!!.first()
                    } catch (e: Exception) {
                        Log.e(ContentValues.TAG, "No devices in device list")
                    }
                }
                try {
                    CommandService().connectToServerFaults(faultCodesViewModel, currentDevice)
                } catch (e: Exception) {
                    Log.e(ContentValues.TAG, "Error Connecting to Server: ", e)
                }
            }
            mainHandler.postDelayed(this, 2000)
        }
    }

    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(updateFaultsTask)
    }

    override fun onResume() {
        super.onResume()
        mainHandler.post(updateFaultsTask)
    }
}
