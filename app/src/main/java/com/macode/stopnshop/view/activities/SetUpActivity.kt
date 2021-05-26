package com.macode.stopnshop.view.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.ActivitySetUpBinding
import com.macode.stopnshop.model.User
import com.macode.stopnshop.utilities.Constants
import com.macode.stopnshop.utilities.SNSTextView
import java.lang.Exception

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

        if (!Places.isInitialized()) {
            Places.initialize(this@SetUpActivity, resources.getString(R.string.google_maps_key))
        }

        binding!!.setUpProfileImage.setOnClickListener(this)
        binding!!.setUpLocationEditInput.setOnClickListener(this)
        binding!!.setUpSaveButton.setOnClickListener(this)

        binding!!.setUpPhoneEditInput.addTextChangedListener(phoneNumberFormattingTextWatcher)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.setUpProfileImage -> {
                imageSelectionDialog(this@SetUpActivity)
            }
            R.id.setUpLocationEditInput -> {
                try {
                    val fields = listOf(
                        Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS
                    )
                    val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(this@SetUpActivity)
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            R.id.setUpSaveButton -> {
                validateUserDetails()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA -> {
                    data?.extras?.let {
                        val bitmap: Bitmap = data.extras!!.get("data") as Bitmap
                        selectedImageUri = convertToImageFile(bitmap)
                        loadUserImage(this@SetUpActivity, selectedImageUri!!, binding!!.setUpProfileImage)
                    }
                }
                GALLERY -> {
                    data?.let {
                        Glide
                            .with(this@SetUpActivity)
                            .load(data.data)
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .listener(object: RequestListener<Drawable> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    Log.e("GallerySelection", "Error loading image!", e)
                                    showErrorSnackBar("Error loading the image from gallery!", true)
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    resource?.let {
                                        val bitmap: Bitmap = resource.toBitmap()
                                        selectedImageUri = convertToImageFile(bitmap)
                                        Log.i("ImagePath", selectedImageUri.toString())
                                    }
                                    return false
                                }
                            })
                            .placeholder(R.drawable.profile_place_holder)
                            .into(binding!!.setUpProfileImage)
                    }
                }
                PLACE_AUTOCOMPLETE_REQUEST_CODE -> {
                    val place: Place = Autocomplete.getPlaceFromIntent(data!!)
                    val regex = "([^,]+), ([A-Z]{2,})".toRegex()
                    val matchResult = regex.find(place.address.toString())
                    binding!!.setUpLocationEditInput.setText(matchResult?.value.toString())
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED){
            Log.e("SetUpActionCancelled", "User cancelled action!")
            showErrorSnackBar("Action cancelled!", true)
        }
    }

    private fun validateUserDetails() {
        val username = binding!!.setUpUsernameEditInput.text.toString()
        val phone = binding!!.setUpPhoneEditInput.text.toString()
        val location = binding!!.setUpLocationEditInput.text.toString()
        val gender = if (binding!!.setUpMaleRadioButton.isChecked) "Male" else "Female"
        when {
            selectedImageUri == null -> {
                showErrorSnackBar("Please select an image!", true)
            }
            username.isEmpty() -> {
                showError(binding!!.setUpUsernameInput, "Please enter a username!")
            }
            phone.isEmpty() -> {
                hideError(binding!!.setUpUsernameInput)
                showError(binding!!.setUpPhoneInput, "Please enter your phone number!")
            }
            location.isEmpty() -> {
                hideError(binding!!.setUpPhoneInput)
                showError(binding!!.setUpLocationInput, "Please enter your location!")
            }
            else -> {
                showProgressDialog("Saving user account info...")
                savingUserInfoToFirebase(selectedImageUri!!, username, phone, location, gender)
            }
        }
    }

    private fun savingUserInfoToFirebase(image: Uri, username: String, phone: String, location: String, gender: String) {
        tokenRef.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                fcmToken = task.result
                profileImageRef.putFile(image).addOnSuccessListener { taskSnapshot ->
                    Log.i("ProfileImageURL", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
                    taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                        Log.i("DownloadableImageURL", uri.toString())
                        profileImageURL = uri.toString()
                        userHashMap["image"] = profileImageURL.toString()
                        userHashMap["username"] = username
                        userHashMap["phone"] = phone
                        userHashMap["location"] = location
                        userHashMap["gender"] = gender
                        userHashMap["fcmToken"] = fcmToken
                        fireStoreClass.updateUserAccountInfo(this@SetUpActivity, userHashMap)
                    }
                }.addOnFailureListener { e ->
                    showErrorSnackBar("${e.message}", true)
                    hideProgressDialog()
                }
            }

        }.addOnFailureListener { e ->
            showErrorSnackBar("${e.message}", true)
            hideProgressDialog()
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
        binding!!.setUpUsernameEditInput.setText(userDetails.username)
        binding!!.setUpPhoneEditInput.setText(userDetails.phone)
        binding!!.setUpLocationEditInput.setText(userDetails.location)
        when (userDetails.gender) {
            "Male" -> {
                binding!!.setUpMaleRadioButton.isChecked = true
                binding!!.setUpFemaleRadioButton.isChecked = false
            }
            "Female" -> {
                binding!!.setUpMaleRadioButton.isChecked = false
                binding!!.setUpFemaleRadioButton.isChecked = true
            }
            else -> {
                binding!!.setUpMaleRadioButton.isChecked = false
                binding!!.setUpFemaleRadioButton.isChecked = false
            }
        }
    }

    fun updateUserSuccess() {
        hideProgressDialog()
        startActivity(Intent(this@SetUpActivity, DashboardActivity::class.java))
        finish()
    }
}