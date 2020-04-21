package com.example.demoapp.ui.setup.ui.ui.protocol

import android.app.AlertDialog
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import androidx.lifecycle.ViewModelProvider
import com.example.demoapp.MainActivity
import com.example.demoapp.R
import com.example.demoapp.ui.bluetooth.REQUEST_ENABLE_BT
import com.example.demoapp.utilities.CommandService
import com.example.demoapp.utilities.DeviceSingleton
import com.github.pires.obd.enums.ObdProtocols

class ProtocolFragment : Fragment() {
    private val protocolViewModel: ProtocolViewModel by viewModels()
    private lateinit var btnFindProtocol: Button
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private lateinit var progressBar: ProgressBar
    private lateinit var mainHandler: Handler
    private lateinit var sharedPref: SharedPreferences

    companion object {
        fun newInstance() = ProtocolFragment()
    }

    private lateinit var viewModel: ProtocolViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPref = requireContext().getSharedPreferences("storedPrefs", Context.MODE_PRIVATE)
        super.onCreate(savedInstanceState)
        mainHandler = Handler(Looper.getMainLooper())
        val previouslyStarted: Boolean = sharedPref.getBoolean("previouslyStarted", false)

        if (previouslyStarted) {
            val device = sharedPref.getString("deviceAddress", "")
            DeviceSingleton.bluetoothDevice =
                bluetoothAdapter?.bondedDevices?.findLast { it -> it.address == device }
            val protocol = sharedPref.getString("odbProtocol", null)
            protocolViewModel.odbProtocol =  if (null != protocol) ObdProtocols.valueOf(protocol) else ObdProtocols.AUTO
        }
        if (DeviceSingleton.bluetoothDevice != null) {
            startActivity(Intent(context, MainActivity::class.java))
            requireActivity().finish()
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
            Dialog.BUTTON_POSITIVE,
            "Continue"
        ) { dialog, which ->
            alert.dismiss()
        }
        alert.show()

        val spinner: Spinner = root.findViewById(R.id.spnSelectDevice)
        pairedDevices(spinner)

        btnFindProtocol = root.findViewById(R.id.btnTryProtocol)
        btnFindProtocol.setOnClickListener { tryConnect() }

        val protocolObserver = Observer<String> { currentProtocolFromOBD ->
            if (currentProtocolFromOBD.contains("OK", true)) {
                Toast.makeText(
                    context,
                    "Connection to " + DeviceSingleton.bluetoothDevice!!.name + " successful",
                    Toast.LENGTH_LONG
                ).show()
                with(sharedPref.edit()) {
                    putString("odbProtocol", protocolViewModel.odbProtocol.toString())
                    putString("deviceAddress", DeviceSingleton.bluetoothDevice!!.address)
                    putBoolean("previouslyStarted", true)
                    apply()
                }
                startActivity(Intent(context, MainActivity::class.java))
                requireActivity().finish()
            }
            mainHandler.postDelayed({
                Toast.makeText(
                    context,
                    "Connection failed. Check your device is paired and connected to the vehicle and try again",
                    Toast.LENGTH_LONG
                ).show()
                progressBar.visibility = View.INVISIBLE
            }, 6000)
        }

        protocolViewModel.returnedProtocol.observe(viewLifecycleOwner, protocolObserver)

        return root
    }

    private fun pairedDevices(spinner: Spinner) {
        val pairedList = bluetoothAdapter?.bondedDevices!!
        val deviceList = ArrayList<String>()
        pairedList.forEach { device ->
            deviceList.add(device.name)

            val adapter = ArrayAdapter(
                this.requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                deviceList
            )
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Log.e("MainActivity", "" + pairedList.size)
                    if (pairedList.isNotEmpty()) {
                        val devices = pairedList.toTypedArray()
                        DeviceSingleton.bluetoothDevice = devices[0]
                        Log.e("MainActivity", "" + DeviceSingleton.bluetoothDevice)
                    }
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    Log.e("MainActivity", "" + pairedList.size)
                    if (pairedList.isNotEmpty()) {
                        val devices = pairedList.toTypedArray()
                        DeviceSingleton.bluetoothDevice = devices[position]
                        Log.e("MainActivity", "" + DeviceSingleton.bluetoothDevice)
                    }
                }
            }
        }
    }

    private fun tryConnect() {
        progressBar.visibility = View.VISIBLE
        try {
            CommandService().connectToServerProtocol(
                protocolViewModel,
                DeviceSingleton.bluetoothDevice!!
            )
        } catch (e: Exception) {
            Toast.makeText(context, "Error Connecting to OBD Device", Toast.LENGTH_LONG).show()
            progressBar.visibility = View.INVISIBLE
            Log.e(TAG, "Error Connecting to Server: ", e)
        }
        mainHandler.postDelayed({
            if (!protocolViewModel.hasConnected) {
                Toast.makeText(
                    context,
                    "Connection failed. Check your device is paired and connected to the vehicle and try again",
                    Toast.LENGTH_LONG
                ).show()
                progressBar.visibility = View.INVISIBLE
            }
        }, 10000)
    }

    override fun onPause() {
        progressBar.visibility = View.INVISIBLE
        mainHandler.removeCallbacksAndMessages(null)
        super.onPause()
    }

    override fun onDestroyView() {
        mainHandler.removeCallbacksAndMessages(null)
        super.onDestroyView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProtocolViewModel::class.java)
    }
}
