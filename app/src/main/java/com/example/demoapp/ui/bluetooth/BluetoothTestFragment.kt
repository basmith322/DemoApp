package com.example.demoapp.ui.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.constraintlayout.widget.Constraints.TAG
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.demoapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import kotlin.collections.ArrayList

const val MESSAGE_READ: Int = 0
const val MESSAGE_WRITE: Int = 1
const val MESSAGE_TOAST: Int = 2

class BluetoothTestFragment : Fragment() {
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var socket: BluetoothSocket? = null
    private val REQUEST_ENABLE_BT = 1
    private val MY_UUID: UUID = UUID.fromString("cab49be8-9b26-4e62-8c78-7d1d8efe279e")
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
            Toast.makeText(context, "This device does not support bluetooth", Toast.LENGTH_LONG)
                .show()
        }

        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }

        btnRefresh = root.findViewById(R.id.btnRefresh)
        btnRefresh.setOnClickListener { pairedDevices() }
        return root
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

    private inner class AcceptThread : Thread() {
        private val mmServerSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
            bluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord("test", MY_UUID)
        }

        override fun run() {
            // Keep listening until exception occurs or a socket is returned.
            var shouldLoop = true
            while (shouldLoop) {
                val socket: BluetoothSocket? = try {
                    mmServerSocket?.accept()
                } catch (e: IOException) {
                    Log.e(TAG, "Socket's accept() method failed", e)
                    shouldLoop = false
                    null
                }
                socket?.also {
                    manageMyConnectedSocket(it)
                    mmServerSocket?.close()
                    shouldLoop = false
                }
            }
        }

        private fun manageMyConnectedSocket(it: BluetoothSocket) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        // Closes the connect socket and causes the thread to finish.
        fun cancel() {
            try {
                mmServerSocket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the connect socket", e)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        navBar.visibility = View.VISIBLE
        AcceptThread().cancel()
    }
}