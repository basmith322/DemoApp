package com.example.demoapp.ui.trip

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.demoapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_logging.*

class LoggingActivity : AppCompatActivity() {
    private lateinit var firebaseDatabase: FirebaseDatabase
    private var dataRetrieve: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logging)

        firebaseDatabase = FirebaseDatabase.getInstance()
    }

    fun basicWrite(view: View) {
        //Obtain values from the text field, load the spinner with objects and load the regions into the spinner at each position
        val testData = 120
        val user = FirebaseAuth.getInstance().currentUser
        var uid = ""
        user?.let { uid = user.uid }
        firebaseDatabase.getReference(uid).child("testData").setValue(testData)
        Toast.makeText(this, "Trip Data Successfully Logged", Toast.LENGTH_LONG).show()
    }

    fun basicRead(view: View) {
        val user = FirebaseAuth.getInstance().currentUser
        var uid = ""
        user?.let { uid = user.uid }
        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseDatabase.getReference(uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val dataMap: HashMap<String, Any> = dataSnapshot.value as HashMap<String, Any>
                    dataRetrieve = dataMap["testData"] as? Long
                }
                textView_TestData.text = dataRetrieve.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                //failed to read value
            }
        })
    }
}
