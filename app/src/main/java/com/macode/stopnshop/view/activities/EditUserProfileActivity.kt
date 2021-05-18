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
import com.macode.stopnshop.databinding.ActivityEditUserProfileBinding
import com.macode.stopnshop.model.User
import com.macode.stopnshop.utilities.Constants
import java.lang.Exception

class EditUserProfileActivity : BaseActivity(), View.OnClickListener {

    private var binding: ActivityEditUserProfileBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditUserProfileBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            userDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        setUpUserDetails(userDetails)

        setUpToolbar()

        if (!Places.isInitialized()) {
            Places.initialize(this@EditUserProfileActivity, resources.getString(R.string.google_maps_key))
        }

        binding!!.editUserImage.setOnClickListener(this@EditUserProfileActivity)
        binding!!.editUserLocationEditInput.setOnClickListener(this@EditUserProfileActivity)
        binding!!.editUserSaveButton.setOnClickListener(this@EditUserProfileActivity)

        binding!!.editUserPhoneEditInput.addTextChangedListener(phoneNumberFormattingTextWatcher)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.editUserImage -> {
                imageSelectionDialog(this@EditUserProfileActivity)
            }
            R.id.editUserLocationEditInput -> {
                try {
                    val fields = listOf(
                        Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS
                    )
                    val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(this@EditUserProfileActivity)
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            R.id.editUserSaveButton -> {
                validateEditedUserDetails()
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
                        loadUserImage(this@EditUserProfileActivity, selectedImageUri!!, binding!!.editUserImage)
                    }
                }
                GALLERY -> {
                    data?.let {
                        Glide
                            .with(this@EditUserProfileActivity)
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
                            .into(binding!!.editUserImage)
                    }
                }
                PLACE_AUTOCOMPLETE_REQUEST_CODE -> {
                    val place: Place = Autocomplete.getPlaceFromIntent(data!!)
                    val regex = "([^,]+), ([A-Z]{2,})".toRegex()
                    val matchResult = regex.find(place.address.toString())
                    binding!!.editUserLocationEditInput.setText(matchResult?.value.toString())
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("EditUserActionCancelled", "User cancelled action!")
            showErrorSnackBar("Action cancelled!", true)
        }
    }

    private fun validateEditedUserDetails() {
        val firstName = binding!!.editUserFirstNameEditInput.text.toString()
        val lastName = binding!!.editUserLastNameEditInput.text.toString()
        val username = binding!!.editUserUsernameEditInput.text.toString()
        val phone = binding!!.editUserPhoneEditInput.text.toString()
        val location = binding!!.editUserLocationEditInput.text.toString()
        val gender = if (binding!!.editUserMaleRadioButton.isChecked) "Male" else "Female"
        when {
            selectedImageUri == null -> {
                showErrorSnackBar("Please select an image!", true)
            }
            firstName.isEmpty() -> {
                showError(binding!!.editUserFirstNameInput, "Please enter your first name!")
            }
            lastName.isEmpty() -> {
                hideError(binding!!.editUserFirstNameInput)
                showError(binding!!.editUserLastNameInput, "Please enter your last name!")
            }
            username.isEmpty() -> {
                hideError(binding!!.editUserLastNameInput)
                showError(binding!!.editUserUsernameInput, "Please enter a username!")
            }
            phone.isEmpty() || phone.length != 14 -> {
                hideError(binding!!.editUserUsernameInput)
                showError(binding!!.editUserPhoneInput, "Please enter your phone number!")
            }
            location.isEmpty() -> {
                hideError(binding!!.editUserPhoneInput)
                showError(binding!!.editUserLocationInput, "Please enter your location!")
            }
            else -> {
               showProgressDialog("Saving user account info...")
               savingEditedUserInfoToFirebase(selectedImageUri!!, firstName, lastName, username, phone, location, gender)
            }
        }
    }

    private fun savingEditedUserInfoToFirebase(
        image: Uri,
        firstName: String,
        lastName: String,
        username: String,
        phone: String,
        location: String,
        gender: String
    ) {
        profileImageRef.putFile(image).addOnSuccessListener { taskSnapshot ->
            Log.i("ProfileImageURL", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                Log.i("DownloadableImageURL", uri.toString())
                profileImageURL = uri.toString()
                userHashMap["image"] = profileImageURL.toString()
                userHashMap["firstName"] = firstName
                userHashMap["lastName"] = lastName
                userHashMap["username"] = username
                userHashMap["phone"] = phone
                userHashMap["location"] = location
                userHashMap["gender"] = gender
                fireStoreClass.updateUserAccountInfo(this@EditUserProfileActivity, userHashMap)
            }
        }.addOnFailureListener { e ->
            hideProgressDialog()
            showErrorSnackBar("${e.message}", true)
        }
    }

    private fun setUpUserDetails(userDetails: User) {
        loadUserImage(this@EditUserProfileActivity, userDetails.image, binding!!.editUserImage)
        // TODO: Error occurs when editing profile image, selectedImageUri is null.
        binding!!.editUserFirstNameEditInput.setText(userDetails.firstName)
        binding!!.editUserLastNameEditInput.setText(userDetails.lastName)
        binding!!.editUserUsernameEditInput.setText(userDetails.username)
        binding!!.editUserPhoneEditInput.setText(userDetails.phone)
        binding!!.editUserLocationEditInput.setText(userDetails.location)
        when (userDetails.gender) {
            "Male" -> {
                binding!!.editUserMaleRadioButton.isChecked = true
                binding!!.editUserFemaleRadioButton.isChecked = false
            }
            "Female" -> {
                binding!!.editUserMaleRadioButton.isChecked = false
                binding!!.editUserFemaleRadioButton.isChecked = true
            }
            else -> {
                binding!!.editUserMaleRadioButton.isChecked = false
                binding!!.editUserFemaleRadioButton.isChecked = false
            }
        }
    }

    private fun setUpToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.editUserToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_white)
        supportActionBar?.title = "Edit Account Information"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"))
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun updateUserSuccess() {
        hideProgressDialog()
        finish()
        startActivity(Intent(this@EditUserProfileActivity, DashboardActivity::class.java))
    }
}