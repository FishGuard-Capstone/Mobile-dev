package com.capstone.fishguard.ui.account

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.capstone.fishguard.MainActivity
import com.capstone.fishguard.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var registerLink: TextView
    private lateinit var googleLoginImage: ImageView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_login)

        initializeViews()
        setupInputValidation()
        setupClickListeners()
        observeLoginState()
    }

    private fun initializeViews() {
        emailInputLayout = findViewById(R.id.emailInputLayout)
        passwordInputLayout = findViewById(R.id.passwordInputLayout)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        registerLink = findViewById(R.id.registerLink)
        googleLoginImage = findViewById(R.id.googleLoginImage)
        progressBar = findViewById(R.id.progressBar) // Pastikan Anda menambahkan ProgressBar di layout
    }

    private fun setupInputValidation() {
        emailInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateEmail()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        passwordInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validatePassword()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun validateEmail(): Boolean {
        val email = emailInput.text.toString().trim()
        return when {
            email.isEmpty() -> {
                emailInputLayout.error = getString(R.string.error_email_empty)
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                emailInputLayout.error = getString(R.string.error_invalid_email)
                false
            }
            else -> {
                emailInputLayout.error = null
                true
            }
        }
    }

    private fun validatePassword(): Boolean {
        val password = passwordInput.text.toString()
        return when {
            password.isEmpty() -> {
                passwordInputLayout.error = getString(R.string.error_password_empty)
                false
            }
            password.length < 6 -> {
                passwordInputLayout.error = getString(R.string.error_password_too_short)
                false
            }
            else -> {
                passwordInputLayout.error = null
                true
            }
        }
    }

    private fun setupClickListeners() {
        loginButton.setOnClickListener {
            if (validateEmail() && validatePassword()) {
                performLogin()
            }
        }

        registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        googleLoginImage.setOnClickListener {
            // Google Sign-In implementation will be added later
        }
    }

    private fun performLogin() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()
        loginViewModel.login(email, password)
    }

    private fun observeLoginState() {
        lifecycleScope.launch {
            loginViewModel.loginState.collect { state ->
                when (state) {
                    is LoginViewModel.LoginState.Idle -> {
                        progressBar.visibility = View.GONE
                        loginButton.isEnabled = true
                    }
                    is LoginViewModel.LoginState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        loginButton.isEnabled = false
                    }
                    is LoginViewModel.LoginState.Success -> {
                        progressBar.visibility = View.GONE
                        loginButton.isEnabled = true
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                    is LoginViewModel.LoginState.Error -> {
                        progressBar.visibility = View.GONE
                        loginButton.isEnabled = true
                        showErrorDialog(state.message)
                    }
                }
            }
        }
    }

    private fun showErrorDialog(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show() // Tampilkan Toast dengan pesan error
    }
}
