package com.example.demoapp.ui.trip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.demoapp.R

class TripFragment : Fragment() {

    private lateinit var tripViewModel: TripViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        tripViewModel =
            ViewModelProviders.of(this).get(TripViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_trip, container, false)
        val textView: TextView = root.findViewById(R.id.text_trip)
        tripViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}