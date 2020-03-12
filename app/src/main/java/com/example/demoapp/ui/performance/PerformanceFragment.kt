package com.example.demoapp.ui.performance

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.demoapp.R
import com.example.demoapp.utilities.MyClientBluetoothService
import kotlinx.android.synthetic.main.fragment_performance.*
import java.util.Timer
import kotlin.concurrent.schedule
import kotlin.concurrent.thread


class PerformanceFragment : Fragment() {
    private val performanceViewModel: PerformanceViewModel by viewModels()
    lateinit var mainHandler: Handler

    private val updatePerformanceTask = object : Runnable {
        override fun run() {
            MyClientBluetoothService().connectToServer(performanceViewModel)
            mainHandler.postDelayed(this, 2000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainHandler = Handler(Looper.getMainLooper())
   }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_performance, container, false)

        //Current Speed Title
        val textCurrentSpeedTitle: TextView = root.findViewById(R.id.textView_CurrentSpeedTitle)
        performanceViewModel.textCurrentSpeedTitle.observe(viewLifecycleOwner, Observer {
            textCurrentSpeedTitle.text = it
        })

        //Current speed value returned from OBD
        val speedObserver = Observer<String> { currentSpeedFromOBD ->
            // Update the UI, in this case, a TextView.
            textView_CurrentSpeed.text = currentSpeedFromOBD
        }
        performanceViewModel.currentSpeed.observe(viewLifecycleOwner, speedObserver)

//        val textCurrentSpeedMph: TextView = root.findViewById(R.id.textView_CurrentSpeed)
//        performanceViewModel.textCurrentSpeed.observe(viewLifecycleOwner, Observer {
//            textCurrentSpeedMph.text = "$it Mph"
//        })

        //Current RPM Title
        val textCurrentRPMTitle: TextView = root.findViewById(R.id.textView_RPMTitle)
        performanceViewModel.textRPMTitle.observe(viewLifecycleOwner, Observer {
            textCurrentRPMTitle.text = it
        })

        //Current RPM value returned from ODB
        val rpmObserver = Observer<String> { currentRPMFromOBD ->
            // Update the UI, in this case, a TextView.
            textView_RPM.text = currentRPMFromOBD
        }
        performanceViewModel.currentRPM.observe(viewLifecycleOwner, rpmObserver)

        //Boost Pressure Title
        val textPSITitle: TextView = root.findViewById(R.id.textView_PSITitle)
        performanceViewModel.textPSITitle.observe(viewLifecycleOwner, Observer {
            textPSITitle.text = it
        })

        //Current Boost Pressure value returned from OBD
        val boostObserver = Observer<String> { currentBoostFromOBD ->
            // Update the UI, in this case, a TextView.
            textView_PSI.text = currentBoostFromOBD
        }
        performanceViewModel.currentBoost.observe(viewLifecycleOwner, boostObserver)

        //Avg Speed
        val textAvgSpeedTitle: TextView = root.findViewById(R.id.textView_AvgSpeedTitle)
        performanceViewModel.textAvgSpeedTitle.observe(viewLifecycleOwner, Observer {
            textAvgSpeedTitle.text = it
        })
        val textAvgSpeed: TextView = root.findViewById(R.id.textView_AvgSpeed)
        performanceViewModel.textAvgSpeed.observe(viewLifecycleOwner, Observer {
            textAvgSpeed.text = "${it}Mph"
        })

        //Max Speed
        val textMaxSpeedTitle: TextView = root.findViewById(R.id.textView_MaxSpeedTitle)
        performanceViewModel.textMaxSpeedTitle.observe(viewLifecycleOwner, Observer {
            textMaxSpeedTitle.text = it
        })
        val textMaxSpeed: TextView = root.findViewById(R.id.textView_MaxSpeed)
        performanceViewModel.textMaxSpeed.observe(viewLifecycleOwner, Observer {
            textMaxSpeed.text = "${it}Mph"

        })
        return root
    }

    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(updatePerformanceTask)
    }

    override fun onResume() {
        super.onResume()
        mainHandler.post(updatePerformanceTask)
    }
}