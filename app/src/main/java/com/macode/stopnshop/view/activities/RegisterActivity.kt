package com.macode.stopnshop.view.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.ActivityRegisterBinding

class RegisterActivity : BaseActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpToolbar()

        binding.registerButton.setOnClickListener {
            validateRegisterDetails()
        }
    }

    private fun setUpToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.registerToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_white)
        supportActionBar?.title = "Register"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"))
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun validateRegisterDetails() {
        val firstName = binding.registerFirstNameEditInput.text.toString()
        val lastName = binding.registerLastNameEditInput.text.toString()
        val email = binding.registerEmailEditInput.text.toString()
        val password = binding.registerPasswordEditInput.text.toString()
        val specialCharacterRegex = "(?=.*[@\$%&#])".toRegex()
        val numberRegex = "(?=.*[0-9])".toRegex()
        val confirmPassword = binding.registerConfirmPasswordEditInput.text.toString()

        when {
            firstName.isEmpty() -> {
                showError(binding.registerFirstNameInput, "Please enter your first name!")
            }
            lastName.isEmpty() -> {
                hideError(binding.registerFirstNameInput)
                showError(binding.registerLastNameInput, "Please enter your last name!")
            }
            email.isEmpty() || !email.contains("@") || !email.contains(".") -> {
                hideError(binding.registerLastNameInput)
                showError(binding.registerEmailInput, "Please enter a valid email address!")
            }
            password.isEmpty() || password.length < 6 -> {
                hideError(binding.registerEmailInput)
                showError(binding.registerPasswordInput, "Password should be longer than 6 characters")
            }
            !password.contains(specialCharacterRegex) -> {
                hideError(binding.registerEmailInput)
                showError(binding.registerPasswordInput, "Password should contain a special character!")
            }
            !password.contains(numberRegex) -> {
                hideError(binding.registerEmailInput)
                showError(binding.registerPasswordInput, "Password should contain a number!")
            }
            confirmPassword != password -> {
                hideError(binding.registerPasswordInput)
                showError(binding.registerConfirmPasswordInput, "Password does not match!")
            }
            !binding.termsAndConditionsCheckBox.isChecked -> {
                hideError(binding.registerConfirmPasswordInput)
                showErrorSnackBar("Please agree to terms and conditions!", true)
            }
            else -> {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        Log.i("Registration", "Registration success")
                        Toast.makeText(this@RegisterActivity, "Registration was successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@RegisterActivity, OnBoardingActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                    } else {
                        Log.e("Registration", "Registration failure", task.exception)
                        Toast.makeText(this@RegisterActivity, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}