package com.macode.stopnshop.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.ActivityOnBoardingBinding
import com.macode.stopnshop.view.adapters.ViewPagerAdapter
import com.macode.stopnshop.view.fragments.AppDescriptionFragment
import com.macode.stopnshop.view.fragments.HeadsUpFragment
import com.macode.stopnshop.view.fragments.WelcomeFragment

class OnBoardingActivity : BaseActivity() {
    private lateinit var binding: ActivityOnBoardingBinding
    private var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val fragmentList = arrayListOf(
            WelcomeFragment(),
            AppDescriptionFragment(),
            HeadsUpFragment(),
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            supportFragmentManager,
            lifecycle
        )

        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) {_, _ ->

        }.attach()

        binding.onBoardingNextButton.setOnClickListener {
            position = binding.viewPager.currentItem
            println(fragmentList.size.toString())
            if (position < fragmentList.size) {
                position++
                println(position.toString())
                binding.viewPager.currentItem = position
            }

            if (position == fragmentList.size - 1) {
                loadLastScreen()
            }
        }

        binding.onBoardingGetStartedButton.setOnClickListener {
            val intent = Intent(this@OnBoardingActivity, SetUpActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun loadLastScreen() {
        binding.onBoardingNextButton.visibility = View.INVISIBLE
        binding.onBoardingGetStartedButton.visibility = View.VISIBLE
    }
}