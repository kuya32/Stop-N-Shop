package com.macode.stopnshop.view.fragments

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.EmailAuthProvider
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.FragmentEditPasswordBinding
import com.macode.stopnshop.view.activities.ForgotPasswordActivity


class EditPasswordFragment : BaseFragment() {

    private var binding: FragmentEditPasswordBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditPasswordBinding.inflate(inflater, container, false)
        val view = binding!!.root

        setUpToolbar(view)

        binding!!.editPasswordForgotPassword.setOnClickListener {
            startActivity(Intent(requireActivity(), ForgotPasswordActivity::class.java))
        }

        binding!!.editPasswordSaveButton.setOnClickListener {
            saveNewPassword()
        }

        return view
    }

    private fun saveNewPassword() {
        val currentPassword = binding!!.editCurrentPasswordEditInput.text.toString()
        val newPassword = binding!!.editNewPasswordEditInput.text.toString()
        val confirmNewPassword = binding!!.editConfirmPasswordEditInput.text.toString()
        val specialCharacterRegex = "(?=.*[@\$%&#])".toRegex()
        val numberRegex = "(?=.*[0-9])".toRegex()

        when {
            currentPassword.isEmpty() || currentPassword.length < 6 || !currentPassword.contains(specialCharacterRegex) || !currentPassword.contains(numberRegex) -> {
                showError(binding!!.editCurrentPasswordInput, "Current password required!")
            }
            currentPassword == newPassword -> {
                hideError(binding!!.editCurrentPasswordInput)
                showError(binding!!.editNewPasswordInput, "New password should be different from the current password!")
            }
            newPassword.isEmpty() || newPassword.length < 6 -> {
                hideError(binding!!.editCurrentPasswordInput)
                showError(binding!!.editNewPasswordInput, "New password should be more than 6 characters!")
            }
            !newPassword.contains(specialCharacterRegex) -> {
                hideError(binding!!.editCurrentPasswordInput)
                showError(binding!!.editNewPasswordInput, "New password should contain a special character!")
            }
            !newPassword.contains(numberRegex) -> {
                hideError(binding!!.editCurrentPasswordInput)
                showError(binding!!.editNewPasswordInput, "New password should contain a number!")
            }
            newPassword != confirmNewPassword -> {
                hideError(binding!!.editNewPasswordInput)
                showError(binding!!.editConfirmPasswordInput, "Confirm password does not match!")
            }
            else -> {
                showProgressDialog("Saving new password...")
                authCredential = EmailAuthProvider.getCredential(firebaseUser?.email.toString(), currentPassword)
                firebaseUser!!.reauthenticate(authCredential).addOnSuccessListener {
                    firebaseUser!!.updatePassword(newPassword).addOnSuccessListener {
                        hideProgressDialog()
                        showErrorSnackBar("Password change successful!", false)
                        Handler(Looper.getMainLooper()).postDelayed({
                            requireActivity().finish()
                            startActivity(requireActivity().intent)
                        }, 1500)
                    }.addOnFailureListener { e ->
                        hideProgressDialog()
                        Log.e(requireActivity().javaClass.simpleName, "Failed to change current password", e)
                        showErrorSnackBar("Failed to change current password!", true)
                    }
                }.addOnFailureListener { e ->
                    hideProgressDialog()
                    Log.e(requireActivity().javaClass.simpleName, "Failed to re-authenticate user. Current password might not match.", e)
                    showErrorSnackBar("Inputted current password does not match!", true)
                }
            }
        }
    }

    private fun setUpToolbar(view: View) {
        val toolbar = view.findViewById<Toolbar>(R.id.editPasswordToolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(
            Color.TRANSPARENT))
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Edit Password"
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"))
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (requireActivity() as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.back_white)
        toolbar.setNavigationOnClickListener {
            val main = requireActivity().findViewById<ScrollView>(R.id.settingsMainRelative)
            val secondary = requireActivity().findViewById<RelativeLayout>(R.id.settingsSecondaryRelative)
            secondary.visibility = View.GONE
            main.visibility = View.VISIBLE
        }
    }

}