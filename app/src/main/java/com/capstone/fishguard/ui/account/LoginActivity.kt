package com.capstone.fishguard.ui.account

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.capstone.fishguard.MainActivity
import com.capstone.fishguard.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_login)

        // Initialize views
        val emailInput = findViewById<TextInputEditText>(R.id.emailInput)
        val passwordInput = findViewById<TextInputEditText>(R.id.passwordInput)
        val loginButton = findViewById<MaterialButton>(R.id.loginButton)
        val registerLink = findViewById<TextView>(R.id.registerLink)

        // Handle login button click
        loginButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            // Perform login logic here
            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Navigate to MainActivity
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                // Show error
                emailInput.error = "Email tidak boleh kosong"
                passwordInput.error = "Password tidak boleh kosong"
            }
        }

        // Handle register link click
        registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
