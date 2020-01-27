package com.example.demoapp.ui.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
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
    private val requestEnableBlueTooth = 1
    private lateinit var btnRefresh: Button
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
            Toast.makeText(context, "This Device doesn't support Bluetooth", Toast.LENGTH_LONG)
                .show()
        }

        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, requestEnableBlueTooth)
        }

        btnRefresh = root.findViewById(R.id.select_device_refresh)
        btnRefresh.setOnClickListener{pairedDevices()}
        return root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        navBar.visibility = View.VISIBLE
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