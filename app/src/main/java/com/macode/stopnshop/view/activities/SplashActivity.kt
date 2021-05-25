package com.macode.stopnshop.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val topSplashAnimation = AnimationUtils.loadAnimation(this, R.anim.top_anim_splash)
        val bottomSplashAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_anim_splash)
        binding.splashLogo.animation = topSplashAnimation
        binding.splashTitle.animation = bottomSplashAnimation
        topSplashAnimation.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(Intent(this@SplashActivity, DashboardActivity::class.java))
                    finish()
                }, 1000)
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }

        })
    }
}