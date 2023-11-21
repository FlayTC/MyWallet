package com.example.mywallet

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private lateinit var usernameEt: EditText
    private lateinit var passwordEt: EditText
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        usernameEt = findViewById(R.id.et_username)
        passwordEt = findViewById(R.id.et_password)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        val savedUsername = sharedPreferences.getString("username", null)
        val savedPassword = sharedPreferences.getString("password", null)

        if (!savedUsername.isNullOrBlank() && !savedPassword.isNullOrBlank()) {
            if (isUserValid(savedUsername, savedPassword)) {
                startHomeActivity()
            } else {
                clearSharedPreferences()
            }
        }
    }

    private fun isUserValid(username: String, password: String): Boolean {
        return username == "admin" && password == "123456"
    }

    private fun startHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun clearSharedPreferences() {
        with(sharedPreferences.edit()) {
            remove("username")
            remove("password")
            apply()
        }
    }

    fun Login(view: View) {
        val username = usernameEt.text.toString().trim()
        val password = passwordEt.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese sus credenciales", Toast.LENGTH_LONG).show()
            return
        }
        with(sharedPreferences.edit()) {
            putString("username", username)
            putString("password", password)
            apply()
        }
        loginUser(username, password)
    }

    private fun loginUser(username: String, password: String) {
        if (username == "admin" && password == "123456") {
            Toast.makeText(this, "Credenciales verificadas", Toast.LENGTH_LONG).show()

            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_LONG).show()
        }
    }
}