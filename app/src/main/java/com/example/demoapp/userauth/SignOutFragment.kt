package com.example.demoapp.userauth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.demoapp.R
import com.google.firebase.auth.FirebaseAuth


class SignOutFragment : Fragment() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var signOutViewModel: SignOutViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        signOutViewModel =
            ViewModelProvider(this).get(SignOutViewModel::class.java)
        val root = inflater.inflate(R.layout.sign_out_fragment, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signOut()
        Toast.makeText(context, "Logout Successful", Toast.LENGTH_SHORT).show()
        startActivity(Intent(context, LoginActivity::class.java))
        return root
    }

}
