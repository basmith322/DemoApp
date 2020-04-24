package com.example.demoapp.ui.setup

import android.bluetooth.BluetoothAdapter
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.demoapp.R
import com.example.demoapp.ui.bluetooth.REQUEST_ENABLE_BT
import com.example.demoapp.ui.setup.ui.ProtocolActivity
import kotlin.system.exitProcess

class BluetoothSetupActivity() : AppCompatActivity() {
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private lateinit var builder: AlertDialog.Builder
    private lateinit var alert: AlertDialog
    private var backPressed: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enable_bluetooth)

        //If BT is already enabled, don't prompt the user
        setTheme(R.style.AppTheme)
        //If there is no Bluetooth Adapter then do not show prompt and inform user the device does not support BT
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "This device does not support bluetooth", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun showAlert() {
        //If BT is already enabled, don't prompt the user
        builder = AlertDialog.Builder(this)
        alert = builder.create()
        alert.setTitle("Enable Bluetooth")
        alert.setMessage("This app requires a Bluetooth connection to function.\nPlease allow the Bluetooth Permission to continue.")
        alert.setButton(
            DialogInterface.BUTTON_POSITIVE,
            "Continue"
        ) { dialogInterface: DialogInterface, i: Int ->
            startBluetooth()
        }
        alert.setButton(
            DialogInterface.BUTTON_NEGATIVE,
            "Exit"
        ) { dialogInterface: DialogInterface, i: Int ->
            exitProcess(0)
        }
        alert.setOnCancelListener {
            alert.cancel()
            finish()
        }
        if (bluetoothAdapter?.isEnabled == false) {
            alert.show()
        } else {
            alert.cancel()
        }
    }

    private fun startBluetooth() {
        //if the device supports bluetooth but adapter is not enabled, request it to be enabled
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(
                enableBtIntent,
                REQUEST_ENABLE_BT
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (bluetoothAdapter?.isEnabled == true) {
            startActivity(Intent(this, ProtocolActivity::class.java))
            finish()
            //If it is not enabled, bring up the alert to ask the user to enable BT or exit
        } else {
            showAlert()
        }
        showAlert()
    }
}