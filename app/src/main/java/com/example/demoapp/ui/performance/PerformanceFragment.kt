package com.example.demoapp.ui.performance

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.demoapp.R
import kotlinx.android.synthetic.main.fragment_performance.*


class PerformanceFragment : Fragment() {

    private lateinit var performanceViewModel: PerformanceViewModel
    private val totalTime: Long = 999999999
    private var countDownInterval: kotlin.Long = 300
    private lateinit var timer: CountDownTimer


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        startCounter(view)
        performanceViewModel =
            ViewModelProviders.of(this).get(PerformanceViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_performance, container, false)

        //Current Speed
        val textCurrentSpeed: TextView = root.findViewById(R.id.text_CurrentSpeedTitle)
        performanceViewModel.textCurrentSpeedTitle.observe(this, Observer {
            textCurrentSpeed.text = it
        })
        val textCurrentSpeedMph: TextView = root.findViewById(R.id.text_CurrentSpeed)
        performanceViewModel.textCurrentSpeed.observe(this, Observer {
            textCurrentSpeedMph.text = "$it Mph"
        })

        //Current RPM
        val textCurrentRPM: TextView = root.findViewById(R.id.text_RPMTitle)
        performanceViewModel.textRPMTitle.observe(this, Observer {
            textCurrentRPM.text = it
        })
        val textRPM: TextView = root.findViewById(R.id.text_RPM)
        performanceViewModel.textRPM.observe(this, Observer {
            textRPM.text = "$it RPM"
        })

        //Boost Pressure
        val textPSITitle: TextView = root.findViewById(R.id.text_PSITitle)
        performanceViewModel.textPSITitle.observe(this, Observer {
            textPSITitle.text = it
        })
        val textPSI: TextView = root.findViewById(R.id.text_PSI)
        performanceViewModel.textPSI.observe(this, Observer {
            textPSI.text = "$it PSI"
        })

        //Avg Speed
        val textAvgSpeedTitle: TextView = root.findViewById(R.id.text_AvgSpeedTitle)
        performanceViewModel.textAvgSpeedTitle.observe(this, Observer {
            textAvgSpeedTitle.text = it
        })
        val textAvgSpeed: TextView = root.findViewById(R.id.text_AvgSpeed)
        performanceViewModel.textAvgSpeed.observe(this, Observer {
            textAvgSpeed.text = "${it}Mph"
        })

        //Max Speed
        val textMaxSpeedTitle: TextView = root.findViewById(R.id.text_MaxSpeedTitle)
        performanceViewModel.textMaxSpeedTitle.observe(this, Observer {
            textMaxSpeedTitle.text = it
        })
        val textMaxSpeed: TextView = root.findViewById(R.id.text_MaxSpeed)
        performanceViewModel.textMaxSpeed.observe(this, Observer {
            textMaxSpeed.text = "${it}Mph"
        })

        return root
    }

    override fun onPause() {
        super.onPause()
        timer.cancel()
    }

    private fun startCounter(view: View?) {
        timer = object : CountDownTimer(totalTime, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                val randomRPM = (600..6000).random()
                text_RPM.text = randomRPM.toString()
                val randomMph = (0..200).random()
                text_CurrentSpeed.text = randomMph.toString()
                val randomPSI = (0..300).random()
                text_PSI.text = randomPSI.toString()
            }

            override fun onFinish() {

            }
        }
        timer.start()
    }
}