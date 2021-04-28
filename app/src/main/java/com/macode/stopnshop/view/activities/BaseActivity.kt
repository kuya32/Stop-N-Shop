package com.macode.stopnshop.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.macode.stopnshop.R

open class BaseActivity : AppCompatActivity() {

    companion object {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    fun showErrorSnackBar(message: String, errorMessage: Boolean) {
        val snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackbarView = snackbar.view

        if (errorMessage) {
            snackbarView.setBackgroundColor(ContextCompat.getColor(this@BaseActivity, R.color.red))
        } else {
            snackbarView.setBackgroundColor(ContextCompat.getColor(this@BaseActivity, R.color.green))
        }
        snackbar.show()
    }

    fun showError(layout: TextInputLayout, message: String) {
        layout.error = message
        layout.requestFocus()
    }

    fun hideError(layout: TextInputLayout) {
        layout.error = null
    }
}