package com.macode.stopnshop.view.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.ActivitySettingsBinding
import com.macode.stopnshop.databinding.SensitiveInfoDialogBinding
import com.macode.stopnshop.model.User
import com.macode.stopnshop.utilities.Constants
import com.macode.stopnshop.view.fragments.EditEmailFragment
import com.macode.stopnshop.view.fragments.EditPasswordFragment

class SettingsActivity : BaseActivity(), View.OnClickListener {

    private var binding: ActivitySettingsBinding? = null
    private lateinit var authCredential: AuthCredential
    private var detailBundle: Bundle = Bundle()
    private var editEmailFragment: EditEmailFragment = EditEmailFragment()
    private var editPasswordFragment: EditPasswordFragment = EditPasswordFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        setUpToolbar()

        binding!!.settingsSensitiveEditButton.setOnClickListener(this@SettingsActivity)
        binding!!.settingsEditButton.setOnClickListener(this@SettingsActivity)
        binding!!.addressesButton.setOnClickListener(this@SettingsActivity)
        binding!!.logoutButton.setOnClickListener(this@SettingsActivity)
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
        userDetails = user

        hideProgressDialog()

        loadUserImage(this@SettingsActivity, user.image, binding!!.settingsProfileImage)
        "${user.firstName} ${user.lastName}".also { binding!!.settingsFullName.text = it }
        binding!!.settingsEmail.text = user.email
        binding!!.settingsUsername.text = user.username
        binding!!.settingsPhone.text = user.phone
        binding!!.settingsLocation.text = user.location
        binding!!.settingsGender.text = user.gender
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.settingsSensitiveEditButton -> {
                sensitiveInfoDialog()
            }
            R.id.settingsEditButton -> {
                val intent = Intent(this@SettingsActivity, EditUserProfileActivity::class.java)
                intent.putExtra(Constants.EXTRA_USER_DETAILS, userDetails)
                startActivity(intent)
            }
            R.id.addressesButton -> {

            }
            R.id.loginButton -> {
                fireStoreClass.logoutUser(this@SettingsActivity)
            }
        }
    }

    private fun sensitiveInfoDialog() {
        val dialog = Dialog(this@SettingsActivity)
        val dialogBinding: SensitiveInfoDialogBinding = SensitiveInfoDialogBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.sensitiveSubmitButton.setOnClickListener {
            val email = dialogBinding.sensitiveEmailEditInput.text.toString()
            val password = dialogBinding.sensitivePasswordEditInput.text.toString()
            val credentials = if (dialogBinding.emailRadioButton.isChecked) "Email" else "Password"
            if (email.isEmpty() || !email.contains("@") || !email.contains(".")) {
                showError(dialogBinding.sensitiveEmailInput, "Please enter a valid email address!")
            } else if (password.isEmpty()) {
                hideError(dialogBinding.sensitiveEmailInput)
                showError(dialogBinding.sensitivePasswordInput, "Please enter your password!")
            } else if (!dialogBinding.emailRadioButton.isChecked && !dialogBinding.passwordRadioButton.isChecked) {
                hideError(dialogBinding.sensitivePasswordInput)
                showErrorSnackBar("Please select credential type to change!", true)
            } else {
                hideError(dialogBinding.sensitivePasswordInput)
                authCredential = EmailAuthProvider.getCredential(email, password)
                firebaseUser?.reauthenticate(authCredential)?.addOnSuccessListener {
                    hideError(dialogBinding.sensitiveEmailInput)
                    hideError(dialogBinding.sensitivePasswordInput)
                    if (credentials == "Email") {
                        dialog.dismiss()
                        binding!!.settingsMainRelative.visibility = View.GONE
                        binding!!.settingsSecondaryRelative.visibility = View.VISIBLE
                        detailBundle.putString("fragmentId", credentials)
                        editEmailFragment.arguments = detailBundle
                        supportFragmentManager.beginTransaction().replace(R.id.settingsFragmentContainer, editEmailFragment, Constants.EMAIL_FRAGMENT).commit()
                    } else if (credentials == "Password") {
                        dialog.dismiss()
                        binding!!.settingsMainRelative.visibility = View.GONE
                        binding!!.settingsSecondaryRelative.visibility = View.VISIBLE
                        detailBundle.putString("fragmentId", credentials)
                        editPasswordFragment.arguments = detailBundle
                        supportFragmentManager.beginTransaction().replace(R.id.settingsFragmentContainer, editPasswordFragment).commit()
                    }
                }?.addOnFailureListener {
                    showError(dialogBinding.sensitiveEmailInput, "Email or password are incorrect!")
                    showError(dialogBinding.sensitivePasswordInput, "Email or password are incorrect!")
                }
            }
        }

        dialog.show()
        val window: Window? = dialog.window
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    fun successfulLogout() {
        val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}