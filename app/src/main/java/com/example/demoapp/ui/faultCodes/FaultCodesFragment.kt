package com.example.demoapp.ui.faultCodes

import android.bluetooth.BluetoothAdapter
import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.demoapp.R
import com.example.demoapp.data.OBDDataBase
import com.example.demoapp.utilities.CommandService
import com.example.demoapp.utilities.DeviceSingleton

class FaultCodesFragment : Fragment() {
    private val faultCodesViewModel: FaultCodesViewModel by viewModels()
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var db: OBDDataBase? = null
    lateinit var mainHandler: Handler
    private lateinit var lv: ListView
    private lateinit var codeDescriptions: MutableList<String>
    private var code: Array<String> = arrayOf()
    private lateinit var btn: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainHandler = Handler(Looper.getMainLooper())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_faults, container, false)

        //Initialize view elements to variables
        btn = root.findViewById(R.id.button_checkFaults)
        btn.setOnClickListener { checkFaultCodes() }
        progressBar = root.findViewById(R.id.progressBar_Faults)
        progressBar.visibility = View.INVISIBLE
        lv = root.findViewById(R.id.listView_Codes)

        //Observer to monitor the value returned from the OBD for protocol
        val faultObserver = Observer<Array<String>> { currentFaultFromOBD ->
            //set code to the values returned from OBD
            code = currentFaultFromOBD
        }
        faultCodesViewModel.faultCode.observe(viewLifecycleOwner, faultObserver)

        return root
    }

    override fun onPause() {
        super.onPause()
        //Stop the handler if the fragment is paused
        mainHandler.removeCallbacks(updateFaultsTask)
    }

    override fun onResume() {
        super.onResume()
        mainHandler.post(updateFaultsTask)
    }

    private fun checkFaultCodes() {
        //if code isn't empty then compare it against the database and return the results to the list
        if (!code.isNullOrEmpty()) {
            try {
                db = OBDDataBase(context, code)
                codeDescriptions = db!!.getDBCodes

                val adapter: ListAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    codeDescriptions
                )
                lv.adapter = adapter
            } catch (e: Exception) {
                Log.e(TAG, "Could not contact db", e)
            }
        } else {
            //If code is empty then inform the user to wait and try again
            Toast.makeText(context, "No Codes found, please wait and try again", Toast.LENGTH_SHORT)
                .show()
        }
    }


    private val updateFaultsTask = object : Runnable {
        override fun run() {
            /*if code is empty then try to perform the command. Stop the handler once the command has run*/
            if (code.isEmpty()) {
                try {
                    CommandService().connectToServerFaults(faultCodesViewModel, DeviceSingleton.bluetoothDevice!!)
                } catch (e: Exception) {
                    Log.e(TAG, "Error Connecting to Server: ", e)
                }
                mainHandler.removeCallbacksAndMessages(null)
            }
            mainHandler.postDelayed(this, 2000)
        }
    }
}
