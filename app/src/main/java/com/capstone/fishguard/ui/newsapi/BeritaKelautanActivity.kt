package com.capstone.fishguard.ui.newsapi

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.fishguard.R
import com.capstone.fishguard.MainActivity
import com.capstone.fishguard.adapter.NewsAdapter

class BeritaKelautanActivity : AppCompatActivity() {

    private lateinit var beritaKelautanViewModel: BeritaKelautanViewModel
    private lateinit var recyclerViewNews: RecyclerView
    private val adapterNews = NewsAdapter()
    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_berita_kelautan)

        // Inisialisasi back button
        backButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            // Kembali ke MainActivity
            navigateToMainActivity()
        }

        recyclerViewNews = findViewById(R.id.newsRV)
        recyclerViewNews.layoutManager = LinearLayoutManager(this)
        recyclerViewNews.adapter = adapterNews

        beritaKelautanViewModel = ViewModelProvider(this)[BeritaKelautanViewModel::class.java]
        beritaKelautanViewModel.getNews()

        beritaKelautanViewModel.news.observe(this, Observer { newsList ->
            newsList?.let {
                adapterNews.submitList(it)
            }
        })
    }

    // Fungsi untuk navigasi ke MainActivity
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

    // Override fungsi onBackPressed untuk menutup aktivitas saat tombol back ditekan
    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        super.onBackPressed()
        navigateToMainActivity()
    }
}