package com.macode.stopnshop.view.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.widget.Toolbar
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : BaseActivity() {

    private var binding: ActivityForgotPasswordBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        setUpToolbar()

        binding!!.forgotPasswordButton.setOnClickListener {
            val email = binding!!.forgotPasswordEmailEditInput.text.toString()
            if (email.isEmpty() || !email.contains("@") || !email.contains(".")) {
                showError(binding!!.forgotPasswordEmailInput, "Please enter a valid email!")
            } else {
                firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener {
                    showErrorSnackBar("Please check your email!", false)
                    Handler(Looper.getMainLooper()).postDelayed({
                        finish()
                    }, 1000)
                }.addOnFailureListener {
                    showErrorSnackBar("Email does not match our records!", true)
                }
            }
        }

        binding!!.backToLogin.setOnClickListener {
            startActivity(Intent(this@ForgotPasswordActivity, LoginActivity::class.java))
            finish()
        }
    }

    private fun setUpToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.forgotPasswordToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_white)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}