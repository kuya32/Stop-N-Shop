package com.macode.stopnshop.view.fragments

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
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.FragmentEditEmailBinding
import com.macode.stopnshop.utilities.Constants

class EditEmailFragment : BaseFragment() {

    private var binding: FragmentEditEmailBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditEmailBinding.inflate(inflater, container, false)
        val view = binding!!.root

        setUpToolbar(view)

        binding!!.editEmailSaveButton.setOnClickListener {
            saveNewEmail()
        }

        return view
    }

    private fun saveNewEmail() {
        val newEmail = binding!!.editEmailEditInput.text.toString()

        if (newEmail.isEmpty() || !newEmail.contains("@") || !newEmail.contains(".")) {
            showError(binding!!.editEmailInput, "Please enter a valid email!")
        } else {
            showProgressDialog("Saving new email...")
            firebaseUser!!.updateEmail(newEmail).addOnSuccessListener {
                firebaseUser!!.sendEmailVerification().addOnSuccessListener {
                    val emailFragment: EditEmailFragment = requireActivity().supportFragmentManager.findFragmentByTag(Constants.EMAIL_FRAGMENT) as EditEmailFragment
                    fireStoreClass.updateUserEmail(emailFragment, newEmail)
                }.addOnFailureListener { e ->
                    hideProgressDialog()
                    Log.e(requireActivity().javaClass.simpleName, "Failed to send verification email", e)
                    showErrorSnackBar("Sorry, we couldn't send verification email!", false)
                }
            }.addOnFailureListener { e ->
                hideProgressDialog()
                Log.e(requireActivity().javaClass.simpleName, "Failed to update user email", e)
                showErrorSnackBar("Sorry, we couldn't update your email!", false)
            }
        }
    }

    private fun setUpToolbar(view: View) {
        val toolbar = view.findViewById<Toolbar>(R.id.editEmailToolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Edit Email"
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

    // TODO: Find a way to inform user about the verification email
    fun updateEmailSuccess() {
        hideProgressDialog()
        showErrorSnackBar("Check your email for verification email!", false)
        Handler(Looper.getMainLooper()).postDelayed({
            requireActivity().finish()
            startActivity(requireActivity().intent)
        }, 1500)
    }
}