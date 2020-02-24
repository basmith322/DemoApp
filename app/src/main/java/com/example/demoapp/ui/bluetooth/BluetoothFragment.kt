package com.example.demoapp.ui.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.se.omapi.SEService.OnConnectedListener
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.demoapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


private const val REQUEST_ENABLE_BT = 1
val MY_UUID: UUID = UUID.fromString("cab49be8-9b26-4e62-8c78-7d1d8efe279e")

class BluetoothTestFragment : Fragment() {
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
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

        //region bluetooth adapter code
        if (bluetoothAdapter == null) {
            Toast.makeText(context, "This device does not support bluetooth", Toast.LENGTH_LONG)
                .show()
        }

        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
        //endregion

        //region button on click listeners
        btnRefresh = root.findViewById(R.id.btnRefresh)
        btnRefresh.setOnClickListener { pairedDevices() }
        //endregion
        return root
    }

    /*function to list all paired devices*/
    private fun pairedDevices() {
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        val deviceList = ArrayList<String>()
        pairedDevices?.forEach { device ->
            deviceList.add(device.name)

            val data = Bundle()
            data.putStringArrayList("deviceName", deviceList)

            val fragment: Fragment = ControlFragment()
            val fragmentManager: FragmentManager? = parentFragmentManager
            fragment.arguments = data
            fragmentManager!!.beginTransaction().replace(R.id.fragmentContainer, fragment).commit()
        }
    }

    private inner class ConnectThread(device: BluetoothDevice) : Thread() {
        private val listener: OnConnectedListener? = null
        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(MY_UUID)
        }

        override fun run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter?.cancelDiscovery()

            mmSocket?.use { socket ->
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                socket.connect()

                // The connection attempt succeeded. Perform work associated with
                // the connection in a separate thread.
                manageMyConnectedSocket(socket)
            }
        }

        private fun manageMyConnectedSocket(socket: BluetoothSocket) {
            //TODO
        }

        // Closes the client socket and causes the thread to finish.
        fun cancel() {
            try {
                mmSocket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the client socket", e)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        navBar.visibility = View.VISIBLE
    }
}