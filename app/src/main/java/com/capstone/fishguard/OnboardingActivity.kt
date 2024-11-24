package com.capstone.fishguard

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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


        repeat(titles.size) {
            tabLayout.addTab(tabLayout.newTab())
        }


        updateContent(imageView, titleView, subtitleView, button, tabLayout)


        button.setOnClickListener {
            pageIndex++
            if (pageIndex < titles.size) {
                updateContent(imageView, titleView, subtitleView, button, tabLayout)
            } else {
                pageIndex = 0
                updateContent(imageView, titleView, subtitleView, button, tabLayout)
            }
        }
    }

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
            0 -> getString(R.string.start)
            else -> getString(R.string.next)
        }


        tabLayout.selectTab(tabLayout.getTabAt(pageIndex))
    }
}