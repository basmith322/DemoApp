package com.example.demoapp.ui

import com.example.demoapp.userAuthentication.LoginActivity
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LoginActivityTest {
    //Test if both email and password are empty
    @Test
    fun validateNoEmailNoPassword() {
        val email = ""
        val password = ""
        val result = LoginActivity().getLoginValidationError(email, password)
        assertNotNull(result)
    }

    //Test if there is no email but a password
    @Test
    fun validateNoEmail() {
        val email = ""
        val password = "test12345"
        val result = LoginActivity().getLoginValidationError(email, password)
        assertNotNull(result)
    }

    //Test if there is an email but no password
    @Test
    fun validateNoPassword() {
        val email = "test1@email.com"
        val password = ""
        val result = LoginActivity().getLoginValidationError(email, password)
        assertNotNull(result)
    }

    //Test if there is both an emal and a password
    @Test
    fun validateEmailAndPassword() {
        val email = "test1@email.com"
        val password = "test12345"
        val result = LoginActivity().getLoginValidationError(email, password)
        assertNull(result)
    }
}