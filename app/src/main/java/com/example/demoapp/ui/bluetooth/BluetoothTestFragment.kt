package com.example.demoapp.ui.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.demoapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView


class BluetoothTestFragment : Fragment() {
    private var bluetoothAdapter: BluetoothAdapter? = null
    private val REQUEST_ENABLE_BT = 1
    private lateinit var btnRefresh: Button
    private lateinit var btnDiscover: Button
    private lateinit var navBar: BottomNavigationView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_bluetooth_test, container, false)

        navBar = activity!!.findViewById(R.id.bottom_nav_view)
        navBar.visibility = View.GONE

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (bluetoothAdapter == null) {
            Toast.makeText(context, "This device does not support bluetooth", Toast.LENGTH_LONG).show()
        }

        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }

        if ((bluetoothAdapter?.isDiscovering!!)) {
            bluetoothAdapter!!.cancelDiscovery()
        }
        bluetoothAdapter!!.startDiscovery()

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        context!!.registerReceiver(receiver, filter)

        btnRefresh = root.findViewById(R.id.btnRefresh)
        btnRefresh.setOnClickListener { pairedDevices() }
        btnDiscover = root.findViewById(R.id.btnDiscover)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        navBar.visibility = View.VISIBLE
        bluetoothAdapter?.cancelDiscovery()

    }

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device.name
                    val deviceHardwareAddress = device.address // MAC address
                    Toast.makeText(context,deviceName,Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun pairedDevices() {
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        val deviceList = ArrayList<String>()
        pairedDevices?.forEach { device ->
            deviceList.add(device.name)

            val data = Bundle()
            data.putStringArrayList("deviceName", deviceList)

            val fragment: Fragment = ControlFragment()
            val fragmentManager: FragmentManager? = fragmentManager
            fragment.arguments = data
            fragmentManager!!.beginTransaction().replace(R.id.fragmentContainer, fragment).commit()
        }
    }
}