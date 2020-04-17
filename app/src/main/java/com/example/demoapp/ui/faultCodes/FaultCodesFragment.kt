package com.example.demoapp.ui.faultCodes

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.ContentValues
import android.database.Cursor
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.demoapp.R
import com.example.demoapp.data.OBDDataBase
import com.example.demoapp.utilities.CommandService


class FaultCodesFragment : Fragment() {
    private val faultCodesViewModel: FaultCodesViewModel by viewModels()
    lateinit var mainHandler: Handler
    private lateinit var data: Bundle
    private lateinit var currentDevice: BluetoothDevice
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private lateinit var lv: ListView

    private var codeDescriptions: Cursor? = null
    private var db: OBDDataBase? = null

    private lateinit var code: Array<String>
    private lateinit var btn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkBtDevices()
        mainHandler = Handler(Looper.getMainLooper())

    }

    private fun checkFaultCodes() {
        db = OBDDataBase(context, code)
        codeDescriptions = db!!.getDBCodes

        val adapter: ListAdapter = SimpleCursorAdapter(
            context,
            android.R.layout.simple_list_item_1,
            codeDescriptions, arrayOf("desc"), intArrayOf(android.R.id.text1), 0
        )
        lv.adapter = adapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fault_codes_fragment, container, false)

        //fault code returned from OBD
        val faultObserver = Observer<Array<String>> { currentFaultFromOBD ->
            // Update the UI, in this case, a TextView.
//            textView_currentFault.text = currentFaultFromOBD
           code = currentFaultFromOBD
        }
        faultCodesViewModel.faultCode.observe(viewLifecycleOwner, faultObserver)

        btn = root.findViewById(R.id.button_Test)
        btn.setOnClickListener { checkFaultCodes() }

        lv = root.findViewById(R.id.listView_Codes)

        return root
    }

    private fun checkBtDevices() {
        if (bluetoothAdapter?.isEnabled == true) {
            try {
                data = arguments!!
                currentDevice = data.get("currentDevice") as BluetoothDevice
            } catch (e: Exception) {
                Log.e(ContentValues.TAG, "Device not yet set, Falling back to default device", e)
                try {
                    val pairedDevices = bluetoothAdapter.bondedDevices
                    currentDevice = pairedDevices!!.first()
                } catch (e: Exception) {
                    Log.e(ContentValues.TAG, "No devices in device list")
                }
            }
        }
    }

    private val updateFaultsTask = object : Runnable {
        override fun run() {
            try {
                CommandService().connectToServerFaults(faultCodesViewModel, currentDevice)
            } catch (e: Exception) {
                Log.e(ContentValues.TAG, "Error Connecting to Server: ", e)
            }
            mainHandler.postDelayed(this, 2000)
        }
    }

    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(updateFaultsTask)
    }

    override fun onResume() {
        checkBtDevices()
        super.onResume()
        mainHandler.post(updateFaultsTask)
    }

    override fun onDestroy() {
        super.onDestroy()
        codeDescriptions?.close()
    }
}
