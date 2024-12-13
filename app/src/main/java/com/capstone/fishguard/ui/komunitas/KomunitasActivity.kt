package com.capstone.fishguard.ui.komunitas

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.capstone.fishguard.R
import com.capstone.fishguard.databinding.ActivityKomunitasBinding
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
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

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Pastikan ListStoryFragment ditampilkan saat pertama kali Activity dibuka
        if (savedInstanceState == null) {
            navController.navigate(R.id.listStoryFragment)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Tidak memuat menu logout
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
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
            supportActionBar?.setDisplayHomeAsUpEnabled(destination.id != R.id.listStoryFragment)
        }
    }

    private fun showToolbar(show: Boolean) {
        binding.toolbarMain.visibility = if (show) View.VISIBLE else View.GONE
    }
}
