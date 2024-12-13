package com.capstone.fishguard.ui.komunitas

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.capstone.fishguard.R
import com.capstone.fishguard.databinding.ActivityKomunitasBinding
import com.capstone.fishguard.SplashActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class KomunitasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKomunitasBinding
    private lateinit var navController: NavController
    private val komunitasViewModel: KomunitasViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKomunitasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        setupNavigation()
        setSupportActionBar(binding.toolbarMain)
        observeViewModel()

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                showLogoutConfirmationDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            showToolbar(destination.id == R.id.listStoryFragment)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            komunitasViewModel.isUserLoggedIn().collect { isLoggedIn ->
                if (!isLoggedIn) navigateToWelcomeScreen()
            }
        }

        lifecycleScope.launch {
            komunitasViewModel.logoutState.collect { state ->
                when (state) {
                    is KomunitasViewModel.LogoutState.Success -> {
                        Toast.makeText(this@KomunitasActivity, "Logout berhasil", Toast.LENGTH_SHORT).show()
                        navigateToWelcomeScreen()
                    }
                    is KomunitasViewModel.LogoutState.Error -> showErrorDialog(state.errorMessage)
                    else -> {}
                }
            }
        }
    }

    private fun navigateToWelcomeScreen() {
        val intent = Intent(this, SplashActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Logout Error")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .create()
            .show()
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Apakah Anda yakin ingin logout?")
            .setPositiveButton("Ya") { _, _ -> komunitasViewModel.logout() }
            .setNegativeButton("Tidak", null)
            .create()
            .show()
    }

    private fun showToolbar(show: Boolean) {
        binding.toolbarMain.visibility = if (show) View.VISIBLE else View.GONE
    }
}