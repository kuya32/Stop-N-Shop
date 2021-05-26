package com.macode.stopnshop.view.fragments

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.macode.stopnshop.R
import com.macode.stopnshop.firebase.FireStoreClass
import com.macode.stopnshop.model.Order

open class BaseFragment : Fragment() {

    private var firebaseAuth = FirebaseAuth.getInstance()
    var firebaseUser: FirebaseUser? = firebaseAuth.currentUser
    lateinit var authCredential: AuthCredential
    val fireStoreClass: FireStoreClass = FireStoreClass()
    private var progressDialog: Dialog? = null
    lateinit var orderItemList: ArrayList<Order>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_base, container, false)
    }

    fun showProgressDialog(text: String) {
        progressDialog = Dialog(requireActivity())
        progressDialog!!.setContentView(R.layout.custom_dialog_progress)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val progressText = progressDialog!!.requireViewById<TextView>(R.id.pleaseWaitText)
            progressText.text = text
        }
        progressDialog!!.show()
    }

    fun hideProgressDialog() {
        progressDialog?.dismiss()
    }

    fun showErrorSnackBar(message: String, errorMessage: Boolean) {
        val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view

        if (errorMessage) {
            snackBarView.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.red))
        } else {
            snackBarView.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.green))
        }
        snackBar.show()
    }

    fun showError(layout: TextInputLayout, message: String) {
        layout.error = message
        layout.requestFocus()
    }

    fun hideError(layout: TextInputLayout) {
        layout.error = null
    }
}