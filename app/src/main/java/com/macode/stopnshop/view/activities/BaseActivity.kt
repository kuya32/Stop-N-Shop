package com.macode.stopnshop.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.macode.stopnshop.R
import java.text.SimpleDateFormat
import java.util.*

open class BaseActivity : AppCompatActivity() {

    companion object {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    fun getDate(): String {
        val sdf = SimpleDateFormat("MM/dd/yyyy hh:mm:ss", Locale.US)
        return sdf.format(Date())
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