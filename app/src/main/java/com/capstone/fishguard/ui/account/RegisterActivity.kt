package com.capstone.fishguard.ui.account

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
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
import java.util.regex.Pattern

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private val registerViewModel: RegisterViewModel by viewModels()

    private lateinit var nameInputLayout: TextInputLayout
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var nameInput: TextInputEditText
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var registerButton: MaterialButton
    private lateinit var loginLink: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_register)

        initializeViews()
        setupInputValidation()
        setupClickListeners()
        observeRegisterState()
    }

    private fun initializeViews() {
        nameInputLayout = findViewById(R.id.nameInputLayout)
        emailInputLayout = findViewById(R.id.emailInputLayout)
        passwordInputLayout = findViewById(R.id.passwordInputLayout)
        nameInput = findViewById(R.id.nameInput)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        registerButton = findViewById(R.id.registerButton)
        loginLink = findViewById(R.id.loginLink)
        progressBar = findViewById(R.id.progressBar) // Pastikan Anda menambahkan ProgressBar di layout
    }

    private fun setupInputValidation() {
        nameInput.addTextChangedListener(createTextWatcher(nameInputLayout) { validateName() })
        emailInput.addTextChangedListener(createTextWatcher(emailInputLayout) { validateEmail() })
        passwordInput.addTextChangedListener(createTextWatcher(passwordInputLayout) { validatePassword() })
    }

    private fun createTextWatcher(
        inputLayout: TextInputLayout,
        validationFunc: () -> Boolean
    ): TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            validationFunc()
        }
        override fun afterTextChanged(s: Editable?) {}
    }

    private fun validateName(): Boolean {
        val name = nameInput.text.toString().trim()
        return when {
            name.isEmpty() -> {
                nameInputLayout.error = getString(R.string.error_name_empty)
                false
            }
            name.length < 3 -> {
                nameInputLayout.error = getString(R.string.error_name_too_short)
                false
            }
            else -> {
                nameInputLayout.error = null
                true
            }
        }
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
            !Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{6,}$").matcher(password).matches() -> {
                passwordInputLayout.error = getString(R.string.error_password_complexity)
                false
            }
            else -> {
                passwordInputLayout.error = null
                true
            }
        }
    }

    private fun setupClickListeners() {
        registerButton.setOnClickListener {
            if (validateName() && validateEmail() && validatePassword()) {
                performRegistration()
            }
        }

        loginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun performRegistration() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()

        registerViewModel.register(email, password)
    }

    private fun observeRegisterState() {
        lifecycleScope.launch {
            registerViewModel.registerState.collect { state ->
                when (state) {
                    is RegisterViewModel.RegisterState.Idle -> {
                        progressBar.visibility = View.GONE
                        registerButton.isEnabled = true
                    }
                    is RegisterViewModel.RegisterState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        registerButton.isEnabled = false
                    }
                    is RegisterViewModel.RegisterState.Success -> {
                        progressBar.visibility = View.GONE
                        registerButton.isEnabled = true
                        showSuccessDialog(state.response.message)
                    }
                    is RegisterViewModel.RegisterState.Error -> {
                        progressBar.visibility = View.GONE
                        registerButton.isEnabled = true
                        showErrorDialog(state.message)
                    }
                }
            }
        }
    }

    private fun showSuccessDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Registrasi Berhasil")
            .setMessage(message)
            .setPositiveButton("Login") { _, _ ->
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            .create()
            .show()
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Registrasi Gagal")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}