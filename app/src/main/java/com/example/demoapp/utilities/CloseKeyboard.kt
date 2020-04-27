package com.example.demoapp.utilities

import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat

/**Simple function that closes the keyboard after a button is pressed*/
class CloseKeyboard {
    fun hideKeyboard(view: View) {
        val imm = ContextCompat.getSystemService(view.context, InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}