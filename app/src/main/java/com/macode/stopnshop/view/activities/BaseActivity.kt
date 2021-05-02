package com.macode.stopnshop.view.activities

import android.app.Dialog
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.macode.stopnshop.R
import com.macode.stopnshop.firebase.FireStoreClass
import com.macode.stopnshop.model.User
import java.text.SimpleDateFormat
import java.util.*

open class BaseActivity : AppCompatActivity() {

    companion object {

    }

    var userDetails: User = User()
    var selectedImageUri: Uri? = null

    val firebaseAuth = FirebaseAuth.getInstance()
    val fireStoreClass: FireStoreClass = FireStoreClass()
    private var progressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    fun getDate(): String {
        val sdf = SimpleDateFormat("MM/dd/yyyy hh:mm:ss", Locale.US)
        return sdf.format(Date())
    }

    fun showProgressDialog(text: String) {
        progressDialog = Dialog(this)
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
        val snackBar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view

        if (errorMessage) {
            snackBarView.setBackgroundColor(ContextCompat.getColor(this@BaseActivity, R.color.red))
        } else {
            snackBarView.setBackgroundColor(ContextCompat.getColor(this@BaseActivity, R.color.green))
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