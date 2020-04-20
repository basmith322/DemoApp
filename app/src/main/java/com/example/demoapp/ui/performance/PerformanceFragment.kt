package com.example.demoapp.ui.performance

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.ContentValues.TAG
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.cardiomood.android.controls.gauge.SpeedometerGauge
import com.example.demoapp.R
import com.example.demoapp.utilities.CommandService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_performance.*
import kotlin.math.roundToInt


class PerformanceFragment : Fragment() {
    private val performanceViewModel: PerformanceViewModel by viewModels()
    lateinit var mainHandler: Handler
    private lateinit var data: Bundle
    private lateinit var currentDevice: BluetoothDevice
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private lateinit var firebaseDatabase: FirebaseDatabase
    private var dataRetrieve: Long? = null
    private var currentMPH: Long = 0
    private var storedMaxSpeed: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainHandler = Handler(Looper.getMainLooper())
        checkBtDevices()
        firebaseDatabase = FirebaseDatabase.getInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_performance, container, false)

        val speedometer: SpeedometerGauge = root.findViewById(R.id.speedometer)

        // Customize SpeedometerGauge
        speedometer.labelConverter =
            SpeedometerGauge.LabelConverter { progress, maxProgress -> (progress.roundToInt()).toString() }
        speedometer.maxSpeed = 220.0
        speedometer.majorTickStep = 20.0
        speedometer.addColoredRange(30.0, 100.0, Color.GREEN)
        speedometer.addColoredRange(100.0, 150.0, Color.YELLOW)
        speedometer.addColoredRange(150.0, 220.0, Color.RED)

        val rpmGauge: SpeedometerGauge = root.findViewById(R.id.rpmGauge)

        // Customize rpmGauge
        rpmGauge.labelConverter =
            SpeedometerGauge.LabelConverter { progress, maxProgress -> (progress.roundToInt()).toString() }
        rpmGauge.maxSpeed = 9000.0
        rpmGauge.majorTickStep = 1000.0
        rpmGauge.addColoredRange(0.0, 4000.0, Color.GREEN)
        rpmGauge.addColoredRange(4000.0, 6000.0, Color.YELLOW)
        rpmGauge.addColoredRange(6000.0, 9000.0, Color.RED)

        val psiGauge: SpeedometerGauge = root.findViewById(R.id.psiGauge)

        // Customize psiGauge
        psiGauge.labelConverter =
            SpeedometerGauge.LabelConverter { progress, maxProgress -> (progress.roundToInt()).toString() }
        psiGauge.maxSpeed = 30.0
        psiGauge.majorTickStep = 10.0
        psiGauge.addColoredRange(0.0, 10.0, Color.GREEN)
        psiGauge.addColoredRange(10.0, 20.0, Color.YELLOW)
        psiGauge.addColoredRange(20.0, 30.0, Color.RED)

        //Current Speed Title
        val textCurrentSpeedTitle: TextView = root.findViewById(R.id.textView_CurrentSpeedTitle)
        performanceViewModel.textCurrentSpeedTitle.observe(viewLifecycleOwner, Observer {
            textCurrentSpeedTitle.text = it
        })

        //Current speed value returned from OBD
        val speedObserver = Observer<Int> { currentSpeedFromOBD ->
            // Update the UI, in this case, a TextView.
            textView_CurrentSpeed.text = "$currentSpeedFromOBD MPH"
            speedometer.speed = currentSpeedFromOBD.toDouble()
            currentMPH = currentSpeedFromOBD.toLong()
        }
        performanceViewModel.currentSpeed.observe(viewLifecycleOwner, speedObserver)

        //Current RPM Title
        val textCurrentRPMTitle: TextView = root.findViewById(R.id.textView_RPMTitle)
        performanceViewModel.textRPMTitle.observe(viewLifecycleOwner, Observer {
            textCurrentRPMTitle.text = it
        })

        //Current RPM value returned from ODB
        val rpmObserver = Observer<Int> { currentRPMFromOBD ->
            // Update the UI, in this case, a TextView.
            textView_RPM.text = "$currentRPMFromOBD RPM"
            rpmGauge.speed = currentRPMFromOBD.toDouble()
        }
        performanceViewModel.currentRPM.observe(viewLifecycleOwner, rpmObserver)

        //Boost Pressure Title
        val textPSITitle: TextView = root.findViewById(R.id.textView_PSITitle)
        performanceViewModel.textPSITitle.observe(viewLifecycleOwner, Observer {
            textPSITitle.text = it
        })

        //Current Boost Pressure value returned from OBD
        val boostObserver = Observer<Int> { currentBoostFromOBD ->
            // Update the UI, in this case, a TextView.
            textView_PSI.text = "$currentBoostFromOBD PSI"
            psiGauge.speed = currentBoostFromOBD.toDouble()
        }
        performanceViewModel.currentBoost.observe(viewLifecycleOwner, boostObserver)

        //Avg Speed
        val textAvgSpeedTitle: TextView = root.findViewById(R.id.textView_AvgSpeedTitle)
        performanceViewModel.textAvgSpeedTitle.observe(viewLifecycleOwner, Observer {
            textAvgSpeedTitle.text = it
        })
        val textAvgSpeed: TextView = root.findViewById(R.id.textView_AvgSpeed)
        performanceViewModel.textAvgSpeed.observe(viewLifecycleOwner, Observer {
            textAvgSpeed.text = it.toString()
        })

        //Max Speed
        val textMaxSpeedTitle: TextView = root.findViewById(R.id.textView_MaxSpeedTitle)
        performanceViewModel.textMaxSpeedTitle.observe(viewLifecycleOwner, Observer {
            textMaxSpeedTitle.text = it
        })
        return root
    }

    private fun checkBtDevices() {
        if (bluetoothAdapter?.isEnabled == true) {
            try {
                data = requireArguments()
                currentDevice = data.get("currentDevice") as BluetoothDevice
            } catch (e: Exception) {
                Log.e(TAG, "Device not yet set, Falling back to default device", e)
                try {
                    val pairedDevices = bluetoothAdapter.bondedDevices
                    currentDevice = pairedDevices!!.first()
                } catch (e: Exception) {
                    Log.e(TAG, "No devices in device list")
                }
            }
        }
    }

    private val updatePerformanceTask = object : Runnable {
        override fun run() {
            try {
                CommandService().connectToServerPerformance(performanceViewModel, currentDevice)
                calcMaxSpeed()
            } catch (e: Exception) {
                Log.e(TAG, "Error Connecting to Server: ", e)
            }
            basicRead()
            mainHandler.postDelayed(this, 2000)
        }
    }

    private fun basicWrite(maxSpeedForFirebase: Long) {
        //Obtain values from the text field, load the spinner with objects and load the regions into the spinner at each position
        val user = FirebaseAuth.getInstance().currentUser
        var uid = ""
        user?.let { uid = user.uid }
        firebaseDatabase.getReference(uid).child("maxMPH").setValue(maxSpeedForFirebase)
        Toast.makeText(context, "Trip Data Successfully Logged", Toast.LENGTH_LONG).show()
    }

    private fun basicRead() {
        val user = FirebaseAuth.getInstance().currentUser
        var uid = ""
        user?.let { uid = user.uid }
        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseDatabase.getReference(uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val dataMap: HashMap<String, Any> = dataSnapshot.value as HashMap<String, Any>
                    dataRetrieve = dataMap["maxMPH"] as? Long
                }
                textView_MaxSpeed.text = dataRetrieve.toString() + " MPH"
                storedMaxSpeed = dataRetrieve!!.toLong()
            }

            override fun onCancelled(error: DatabaseError) {
                //failed to read value
            }
        })
    }

    private fun calcMaxSpeed() {
        if (storedMaxSpeed < currentMPH) {
            storedMaxSpeed = currentMPH
            basicWrite(storedMaxSpeed)
        }
    }

    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(updatePerformanceTask)
    }

    override fun onResume() {
        super.onResume()
        checkBtDevices()
        mainHandler.post(updatePerformanceTask)
    }
}