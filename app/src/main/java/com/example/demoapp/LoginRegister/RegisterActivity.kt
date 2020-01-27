package com.example.demoapp.LoginRegister

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.example.demoapp.MainActivity
import com.example.demoapp.R
import com.example.demoapp.Utilities.CloseKeyboard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

class RegisterActivity : AppCompatActivity() {
    private lateinit var fireBaseAuth: FirebaseAuth
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //Initialize Login Authentication
        fireBaseAuth = FirebaseAuth.getInstance()

        //Find the progress bar and hide it until necessary
        progressBar = findViewById(R.id.progressBar_reg)
        progressBar.visibility = View.INVISIBLE
    }

    //Perform validation check if there are email and password strings. Email and Password formatting are handled by Firebase.
    fun getRegisterValidationError(email: String, password: String): String? {
        if (email == "") {
            return "Please enter a valid email address"
        }
        if (password == "") {
            return "Please enter a valid password"
        }
        return null
    }

    fun registerUser(view: View) {
        //Obtain email and password from text fields and pass them to validation check function
        val email = findViewById<EditText>(R.id.editTextRegEmail).text.toString()
        val password = findViewById<EditText>(R.id.editTextRegPassword).text.toString()
        val validationError = getRegisterValidationError(email, password)

        //Only allow the user to continue if the values pass validation check.
        if (validationError != null) {
            Toast.makeText(this, validationError, Toast.LENGTH_LONG).show()
            return
        }

        CloseKeyboard().hideKeyboard(view)
        progressBar.visibility = View.VISIBLE

        //Attempt Register. If formatting is not correct or there is a Firebase error, user will not be registered.
        fireBaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this@RegisterActivity) { task ->
                //checking if successful
                if (task.isSuccessful) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@RegisterActivity, "Registration Successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                    finish()
                } else {
                    val e = task.exception as FirebaseAuthException
                    Toast.makeText(this@RegisterActivity, "Registration Failed: " + e.message, Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun goLogin(view: View) {
        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
    }
}
