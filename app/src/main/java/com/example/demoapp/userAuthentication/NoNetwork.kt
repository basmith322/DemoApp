package com.example.demoapp.userAuthentication

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.demoapp.R

class NoNetwork : AppCompatActivity() {
    //Activity used to simply show if there is a network failure.
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_network)
    }

    fun retry(v: View) {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
