package com.macode.stopnshop.view.activities

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private var binding: ActivitySettingsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        setUpToolbar()
    }

    private fun setUpToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.settingsToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_white)
        supportActionBar?.title = "Settings"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"))
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}