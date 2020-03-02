package com.example.demoapp.ui.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.demoapp.MainActivity
import com.example.demoapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView


class BluetoothTestFragment : Fragment() {
    private val handler: Handler = Handler(Looper.getMainLooper())
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private lateinit var btnPaired: Button
    private lateinit var btnServer: Button
    private lateinit var btnPair: Button
    private lateinit var btnSend: Button
    private lateinit var navBar: BottomNavigationView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
                // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_bluetooth_test, container, false)
        navBar = activity!!.findViewById(R.id.bottom_nav_view)
        navBar.visibility = View.GONE

        view_data = root.findViewById(R.id.textView2)

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
        btnPaired = root.findViewById(R.id.btnPaired)
        btnPaired.setOnClickListener { pairedDevices() }
        btnServer = root.findViewById(R.id.btnServerStart)
        btnServer.setOnClickListener { MyBluetoothService(handler).startServer() }
        btnPair = root.findViewById(R.id.btnPair)
        btnPair.setOnClickListener { MyBluetoothService(handler).pairDevice() }
        btnSend = root.findViewById(R.id.btnSend)
        btnSend.setOnClickListener { MyBluetoothService(handler).sendMessage() }
        //endregion

        return root
    }

    private fun pairedDevices() {
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        val deviceList = java.util.ArrayList<String>()
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

    override fun onDestroyView() {
        super.onDestroyView()
        navBar.visibility = View.VISIBLE
        MyBluetoothService(handler).AcceptThread().cancel()
//        BTBackend().ConnectedThread().cancel()
    }
}