package com.macode.stopnshop.view.activities

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.ActivitySetUpBinding
import com.macode.stopnshop.databinding.ImageSelectionDialogBinding
import com.macode.stopnshop.model.User
import com.macode.stopnshop.utilities.Constants
import com.macode.stopnshop.utilities.SNSTextView

class SetUpActivity : BaseActivity(), View.OnClickListener {

    private var binding: ActivitySetUpBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetUpBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            userDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        setUpUserDetails(userDetails)

        setUpToolbar()

        binding!!.setUpProfileImage.setOnClickListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA) {
                data?.extras?.let {
                    val bitmap: Bitmap = data.extras!!.get("data") as Bitmap
                    Glide
                        .with(this@SetUpActivity)
                        .load(bitmap)
                        .centerCrop()
                        .placeholder(R.drawable.profile_place_holder)
                        .into(binding!!.setUpProfileImage)

                    selectedImageUri = convertToImageFile(bitmap)
                }
            }
        }
    }

    private fun setUpToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.setUpToolbar)
        val toolbarTitle = toolbar.findViewById<SNSTextView>(R.id.toolbarTitle)
        toolbarTitle.text = resources.getString(R.string.setUpTitle)
        toolbarTitle.setTextColor(resources.getColor(R.color.primaryColor, theme))
        setSupportActionBar(toolbar)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
    }

    private fun setUpUserDetails(userDetails: User) {
//        selectedImageUri = userDetails.image
//        Glide
//            .with(this@SetUpActivity)
//            .load(selectedImageUri)
        binding!!.setUpUsernameEditInput.setText(userDetails.username)
        binding!!.setUpPhoneEditInput.setText(userDetails.phone)
        binding!!.setUpLocationEditInput.setText(userDetails.location)
        if (userDetails.gender == "Female") {
            binding!!.maleRadioButton.isChecked = false
            binding!!.femaleRadioButton.isChecked = true
        } else {
            binding!!.maleRadioButton.isChecked = true
            binding!!.femaleRadioButton.isChecked = false
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.setUpProfileImage -> {
                imageSelectionDialog(this@SetUpActivity)
            }
        }
    }


}