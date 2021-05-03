package com.macode.stopnshop.view.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseUser
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.ActivityRegisterBinding
import com.macode.stopnshop.model.User

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

        binding.login.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
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
                showProgressDialog("Registering new user...")
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        firebaseAuth.currentUser!!.sendEmailVerification()
                        Log.i("Registration", "Registration success")
                        showErrorSnackBar("Registration was successful. Check your email for a verification link!", false)
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val user = User()
                        user.id = firebaseUser.uid
                        user.dateUserCreated = getDate()
                        user.firstName = firstName
                        user.lastName = lastName
                        user.email = email
                        Handler(Looper.getMainLooper()).postDelayed({
                            fireStoreClass.registerUser(this@RegisterActivity, user)
                        }, 1500)

                    } else {
                        hideProgressDialog()
                        Log.e("Registration", "Registration failure", task.exception)
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
            }
        }
    }

    fun successfulLogout() {
        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}