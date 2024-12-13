// OnboardingActivity.kt
package com.capstone.fishguard

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.capstone.fishguard.ui.account.LoginActivity
import com.google.android.material.tabs.TabLayout

class OnboardingActivity : AppCompatActivity() {
    private var pageIndex = 0

    private val images = arrayOf(R.drawable.onboard_1, R.drawable.onboard_2, R.drawable.onboard_3)
    private lateinit var titles: Array<String>
    private lateinit var subtitles: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

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
                navigateToLogin()
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
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        imageView.setImageResource(images[pageIndex])
        imageView.startAnimation(fadeIn)

        titleView.text = titles[pageIndex]
        subtitleView.text = subtitles[pageIndex]

        titleView.startAnimation(fadeIn)
        subtitleView.startAnimation(fadeIn)

        button.text = if (pageIndex == titles.lastIndex) getString(R.string.start) else getString(R.string.next)
        tabLayout.selectTab(tabLayout.getTabAt(pageIndex))
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
