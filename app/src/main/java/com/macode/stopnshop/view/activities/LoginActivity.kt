package com.macode.stopnshop.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.ActivityLoginBinding
import com.macode.stopnshop.model.User

class LoginActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginForgotPassword.setOnClickListener(this)
        binding.loginButton.setOnClickListener(this)
        binding.register.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.loginForgotPassword -> {
                startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
            }
            R.id.loginButton -> {
                val email = binding.loginEmailEditInput.text.toString()
                val password = binding.loginPasswordEditInput.text.toString()
                if (email.isEmpty() || !email.contains("@") || !email.contains(".")) {
                    showError(binding.loginEmailInput, "Please enter a valid email address!")
                } else if (password.isEmpty()) {
                    hideError(binding.loginEmailInput)
                    showError(binding.loginPasswordInput, "Please enter your password!")
                } else {
                    hideError(binding.loginPasswordInput)
                    loginUser(email, password)
                }

            }
            R.id.register -> {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        showProgressDialog("Logging in user...")
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            if (firebaseAuth.currentUser!!.isEmailVerified) {
                fireStoreClass.establishUser(this@LoginActivity)
                Log.i("Login", "User login was successful!")
            } else {
                showErrorSnackBar("Please check your email to verify account!", true)
            }
        }.addOnFailureListener { e ->
            Log.e("Login", "Login user failed", e)
            showErrorSnackBar(e.message.toString(), true)
        }
    }

    fun loginSuccess(loggedUser: User) {
        hideProgressDialog()
        val image = loggedUser.image
        val username = loggedUser.username
        val location = loggedUser.location
        val phone = loggedUser.phone

        if (image != "" && username != "" && location != "" && phone != "") {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        } else {
            startActivity(Intent(this@LoginActivity, OnBoardingActivity::class.java))
            finish()
        }
        finish()
    }
}