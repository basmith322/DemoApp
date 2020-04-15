package com.example.demoapp.ui.setup.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.demoapp.R
import com.example.demoapp.ui.setup.ui.ui.protocol.ProtocolFragment

class ProtocolActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.protocol_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ProtocolFragment.newInstance())
                .commitNow()
        }
    }
}