package com.example.demoapp.ui.battery

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.demoapp.R


class BatteryFragment : Fragment() {

    private lateinit var batteryViewModel: BatteryViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        batteryViewModel =
            ViewModelProvider(this).get(BatteryViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_battery, container, false)
        val textView: TextView = root.findViewById(R.id.text_battery)
        batteryViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}