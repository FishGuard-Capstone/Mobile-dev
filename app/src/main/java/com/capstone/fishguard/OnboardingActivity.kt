// OnboardingActivity.kt
package com.capstone.fishguard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.capstone.fishguard.ui.account.LoginActivity
import com.google.android.material.tabs.TabLayout

class OnboardingActivity : AppCompatActivity() {
    private var pageIndex = 0

    // Data gambar
    private val images = arrayOf(R.drawable.onboard_1, R.drawable.onboard_2, R.drawable.onboard_3)

    // Data teks
    private lateinit var titles: Array<String>
    private lateinit var subtitles: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        // Inisialisasi teks
        titles = arrayOf(
            getString(R.string.onboarding_title_1),
            getString(R.string.onboarding_title_2),
            getString(R.string.onboarding_title_3)
        )
        subtitles = arrayOf(
            getString(R.string.onboarding_subtitle_1),
            getString(R.string.onboarding_subtitle_2),
            getString(R.string.onboarding_subtitle_3)
        )

        val imageView: ImageView = findViewById(R.id.imageView)
        val titleView: TextView = findViewById(R.id.titleText)
        val subtitleView: TextView = findViewById(R.id.subtitleText)
        val button: Button = findViewById(R.id.actionButton)
        val tabLayout: TabLayout = findViewById(R.id.tabLayout)

        // Menambahkan tab untuk setiap halaman onboarding
        repeat(titles.size) {
            tabLayout.addTab(tabLayout.newTab())
        }

        // Memperbarui konten tampilan onboarding
        updateContent(imageView, titleView, subtitleView, button, tabLayout)

        button.setOnClickListener {
            pageIndex++
            if (pageIndex < titles.size) {
                // Jika halaman masih ada, perbarui tampilan untuk halaman selanjutnya
                updateContent(imageView, titleView, subtitleView, button, tabLayout)
            } else {
                // Jika sudah di halaman terakhir, navigasikan ke halaman login
                navigateToLogin()
            }
        }
    }

    // Fungsi untuk memperbarui konten tampilan berdasarkan halaman yang aktif
    private fun updateContent(
        imageView: ImageView,
        titleView: TextView,
        subtitleView: TextView,
        button: Button,
        tabLayout: TabLayout
    ) {
        imageView.setImageResource(images[pageIndex])
        titleView.text = titles[pageIndex]
        subtitleView.text = subtitles[pageIndex]

        button.text = when (pageIndex) {
            0 -> getString(R.string.start) // Tombol di halaman pertama
            else -> getString(R.string.next) // Tombol di halaman selain pertama
        }

        // Memilih tab yang sesuai dengan halaman yang aktif
        tabLayout.selectTab(tabLayout.getTabAt(pageIndex))
    }

    // Fungsi untuk menavigasi ke halaman login setelah onboarding selesai
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}