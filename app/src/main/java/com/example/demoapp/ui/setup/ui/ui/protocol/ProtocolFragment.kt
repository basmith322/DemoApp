package com.example.demoapp.ui.setup.ui.ui.protocol

import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.demoapp.MainActivity
import com.example.demoapp.R
import com.example.demoapp.ui.bluetooth.REQUEST_ENABLE_BT
import com.example.demoapp.utilities.CommandService


class ProtocolFragment : Fragment() {
    private val protocolViewModel: ProtocolViewModel by viewModels()
    private lateinit var currentDevice: BluetoothDevice
    private lateinit var btnFindProtocol: Button
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private lateinit var progressBar: ProgressBar


    companion object {
        fun newInstance() = ProtocolFragment()
    }

    private lateinit var viewModel: ProtocolViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        var previouslyStarted: Boolean = sharedPref.getBoolean("started", false)
        if (!previouslyStarted) {
            with (sharedPref.edit()){
                previouslyStarted = true
                putBoolean("started",previouslyStarted)
                apply()
            }
        } else {
            val started = sharedPref.getBoolean("started", true)
            if (started) {
                startActivity(Intent(context, MainActivity::class.java))
                activity!!.finish()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.protocol_fragment, container, false)

        progressBar = root.findViewById(R.id.progressBar_ObdOK)
        progressBar.visibility = View.INVISIBLE

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

        val builder = AlertDialog.Builder(context)
        val alert = builder.create()
        alert.setTitle("First Time Setup")
        alert.setMessage(
            "As part of the setup process, DigiDash will now attempt to automatically find the correct OBD protocol for your device." +
                    "\nPlease select the OBD device from the list and then tap the Find Protocol button. " +
                    "\nYou can add or change your device in the Bluetooth page later if required"
        )
        alert.setButton(
            "Continue"
        ) { dialog, which ->
            alert.dismiss()
        }
        alert.show()

        val spinner: Spinner = root.findViewById(R.id.spnSelectDevice)
        pairedDevices(spinner)

        btnFindProtocol = root.findViewById(R.id.btnTryProtocol)
        btnFindProtocol.setOnClickListener { updateProtocolTask() }

        val protocolObserver = Observer<String> { currentProtocolFromOBD ->
            if (currentProtocolFromOBD == "OK") {
                startActivity(Intent(context, MainActivity::class.java))
                activity!!.finish()
            } else {
                Toast.makeText(context, "OK not received from OBD", Toast.LENGTH_SHORT).show()
            }
        }
        protocolViewModel.returnedProtocol.observe(viewLifecycleOwner, protocolObserver)

        return root
    }

    private fun pairedDevices(spinner: Spinner) {
        val pairedList = bluetoothAdapter?.bondedDevices
        val deviceList = java.util.ArrayList<String>()
        pairedList?.forEach { device ->
            deviceList.add(device.name)

            val adapter = ArrayAdapter(
                context!!,
                android.R.layout.simple_spinner_dropdown_item,
                deviceList
            )
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Log.e("MainActivity", "" + pairedList.size)
                    if (pairedList.size > 0) {
                        val devices: Array<Any> = pairedList.toTypedArray()
                        currentDevice = devices[0] as BluetoothDevice
                        Log.e(TAG, "" + currentDevice)
                    }
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    Log.e("MainActivity", "" + pairedList.size)
                    if (pairedList.size > 0) {
                        val devices: Array<Any> = pairedList.toTypedArray()
                        currentDevice = devices[position] as BluetoothDevice
                        Log.e(TAG, "" + currentDevice)
                    }
                }
            }
        }
    }

    private fun updateProtocolTask() {
        progressBar.visibility = View.VISIBLE
        try {
            CommandService().connectToServerProtocol(protocolViewModel, currentDevice)
            Toast.makeText(context, "Connection to " + currentDevice.name + " successful", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error Connecting to OBD Device", Toast.LENGTH_LONG).show()
            progressBar.visibility = View.INVISIBLE
            Log.e(TAG, "Error Connecting to Server: ", e)
        }
    }

    override fun onPause() {
        super.onPause()
        progressBar.visibility = View.INVISIBLE
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProtocolViewModel::class.java)
    }
}
