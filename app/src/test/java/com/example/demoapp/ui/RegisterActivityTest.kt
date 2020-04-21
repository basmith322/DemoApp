package com.example.demoapp.ui

import com.example.demoapp.userAuthentication.RegisterActivity
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class RegisterActivityTest {
    @Test
    fun validateNoEmailNoPassword() {
        val email = ""
        val password = ""
        val confirmPassword = ""
        val result = RegisterActivity().getRegisterValidationError(email, password, confirmPassword)
        assertNotNull(result)
    }

    //Test if there is no email but a password
    @Test
    fun validateNoEmail() {
        val email = ""
        val password = "test12345"
        val confirmPassword = "test12345"
        val result = RegisterActivity().getRegisterValidationError(email, password, confirmPassword)
        assertNotNull(result)
    }

    //Test if there is an email but no password
    @Test
    fun validateNoPassword() {
        val email = "test1@email.com"
        val password = ""
        val confirmPassword = ""
        val result = RegisterActivity().getRegisterValidationError(email, password, confirmPassword)
        assertNotNull(result)
    }

    //Test if there is both an email and a password
    @Test
    fun validateEmailAndPassword() {
        val email = "test1@email.com"
        val password = "test12345"
        val confirmPassword = "test12345"
        val result = RegisterActivity().getRegisterValidationError(email, password, confirmPassword)
        assertNull(result)
    }

    @Test
    fun validateConfirmedPassWrong() {
        val email = "testemail@email.com"
        val password = "test12345"
        val confirmPassword = "test12354"
        val result = RegisterActivity().getRegisterValidationError(email, password, confirmPassword)
        assertNotNull(result)
    }
}