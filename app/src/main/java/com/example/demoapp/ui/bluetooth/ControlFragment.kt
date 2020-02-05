package com.example.demoapp.ui.bluetooth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.demoapp.R


class ControlFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_control, container, false)

        val data: Bundle? = arguments

        val deviceName: TextView = root.findViewById(R.id.textView_DeviceName)
        deviceName.text = data!!.get(String()).toString()
        val deviceListView: ListView = root.findViewById(R.id.list_Devices)
        val deviceList = data.getStringArrayList("deviceName")
        val listItems = arrayOfNulls<String>(deviceList!!.size)

        for (i in 0 until deviceList.size) {
            val device = deviceList[i]
            listItems[i] = device.toString()
        }

        val adapter = ArrayAdapter<String>(context!!, android.R.layout.simple_list_item_1, listItems)
        deviceListView.adapter = adapter
        return root
    }
}
