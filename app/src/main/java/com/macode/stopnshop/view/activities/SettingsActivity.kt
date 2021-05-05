package com.macode.stopnshop.view.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.ActivitySettingsBinding
import com.macode.stopnshop.model.User

class SettingsActivity : BaseActivity() {

    private var binding: ActivitySettingsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        setUpToolbar()
    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
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

    private fun getUserDetails() {
        showProgressDialog("Retrieving user details...")
        fireStoreClass.establishUser(this@SettingsActivity)
    }

    fun userDetailsSuccess(user: User) {
        hideProgressDialog()

        loadUserImage(this@SettingsActivity, user.image, binding!!.settingsProfileImage)
        "${user.firstName} ${user.lastName}".also { binding!!.settingsFullName.text = it }
        binding!!.settingsEmail.text = user.email
        binding!!.settingsUsername.text = user.username
        binding!!.settingsPhone.text = user.phone
        binding!!.settingsLocation.text = user.location
        binding!!.settingsGender.text = user.gender
    }

    fun successfulLogout() {
        val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}