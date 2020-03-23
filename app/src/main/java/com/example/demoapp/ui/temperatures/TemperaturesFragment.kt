package com.example.demoapp.ui.temperatures

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.demoapp.R

class TemperaturesFragment : Fragment() {

    private lateinit var temperaturesViewModel: TemperaturesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        temperaturesViewModel =
            ViewModelProvider(this).get(TemperaturesViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_temperatures, container, false)


        return root
    }

}
