package com.example.demoapp.ui.consumption

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.demoapp.R

class ConsumptionFragment : Fragment() {

    private lateinit var consumptionViewModel: ConsumptionViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        consumptionViewModel =
            ViewModelProvider(this).get(ConsumptionViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_consumption, container, false)
        val textView: TextView = root.findViewById(R.id.text_consumption)
        consumptionViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}
