package com.macode.stopnshop.view.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.ActivityAddEditAddressBinding
import com.macode.stopnshop.model.Address
import com.macode.stopnshop.utilities.Constants
import java.lang.Exception

class AddEditAddressActivity : BaseActivity() {

    private var binding: ActivityAddEditAddressBinding? = null
    private var stateSelected: String = ""
    private var lat: String = ""
    private var long: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditAddressBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        addStateSpinner()

        if (intent.hasExtra(Constants.ADDRESS_DETAILS)) {
            addressDetails = intent.getParcelableExtra(Constants.ADDRESS_DETAILS)!!
            establishAddressInfo(addressDetails)
        }

        setUpToolbar()

        if (!Places.isInitialized()) {
            Places.initialize(this@AddEditAddressActivity, resources.getString(R.string.google_maps_key))
        }

        binding!!.addEditAddressPhoneEditInput.addTextChangedListener(phoneNumberFormattingTextWatcher)

        binding!!.addEditAddressEditInput.setOnClickListener {
            try {
                val fields = listOf(
                    Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS
                )
                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(this@AddEditAddressActivity)
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding!!.addEditAddressSubmitButton.setOnClickListener {
            validateAddressDetails()
        }

        binding!!.addEditAddressTypeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.addEditAddressOtherRadioButton) {
                binding!!.addEditAddressOtherDetailsInput.visibility = View.VISIBLE
            } else {
                binding!!.addEditAddressOtherDetailsInput.visibility = View.GONE
            }
        }
    }

    private fun establishAddressInfo(addressDetails: Address) {
        binding!!.addEditAddressFullNameEditInput.setText(addressDetails.name)
        binding!!.addEditAddressPhoneEditInput.setText(addressDetails.phone)
        binding!!.addEditAddressEditInput.setText(addressDetails.address)
        binding!!.addEditAddressCityEditInput.setText(addressDetails.city)
        stateSelected = addressDetails.state
        binding!!.addEditStateSpinner.setSelection(resources.getStringArray(R.array.states).indexOf(addressDetails.state))
        binding!!.addEditAddressZipEditInput.setText(addressDetails.zipcode)
        binding!!.addEditAddressAdditionalEditInput.setText(addressDetails.additionalInfo)
        when (addressDetails.type) {
            "Home" -> {
                binding!!.addEditAddressHomeRadioButton.isChecked = true
                binding!!.addEditAddressOfficeRadioButton.isChecked = false
                binding!!.addEditAddressOtherRadioButton.isChecked = false
            }
            "Office" -> {
                binding!!.addEditAddressHomeRadioButton.isChecked = false
                binding!!.addEditAddressOfficeRadioButton.isChecked = true
                binding!!.addEditAddressOtherRadioButton.isChecked = false
            }
            "Other" -> {
                binding!!.addEditAddressHomeRadioButton.isChecked = false
                binding!!.addEditAddressOfficeRadioButton.isChecked = false
                binding!!.addEditAddressOtherRadioButton.isChecked = true
                binding!!.addEditAddressOtherDetailsInput.visibility = View.VISIBLE
                binding!!.addEditAddressOtherDetailsEditInput.setText(addressDetails.otherDetails)
            }
        }
        when (addressDetails.default) {
            "true" -> {
                binding!!.addEditDefaultCheckBox.isChecked = true
            }
            else -> {
                binding!!.addEditDefaultCheckBox.isChecked = false
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PLACE_AUTOCOMPLETE_REQUEST_CODE -> {
                    val place: Place = Autocomplete.getPlaceFromIntent(data!!)
                    var latLng = place.latLng.toString()
                    latLng = latLng.replace("lat/lng: ", "")
                    latLng = latLng.replace("(", "")
                    latLng = latLng.replace(")", "")
                    val split = latLng.split(",")
                    lat = split[0]
                    long = split[1]
                    binding!!.addEditAddressEditInput.setText(place.name.toString())
                    val regex = "([^,]+), ([A-Z]{2,})".toRegex()
                    val zipRegex ="\\b\\d{5}\\b".toRegex()
                    val matchResult = regex.find(place.address.toString())?.value.toString()
                    val city = matchResult.substring(1, matchResult.indexOf(","))
                    val state = matchResult.substring(matchResult.indexOf(",") + 2)
                    val zipCodeResult = zipRegex.find(place.address.toString())?.value.toString()
                    stateSelected = state
                    binding!!.addEditAddressCityEditInput.setText(city)
                    binding!!.addEditStateSpinner.setSelection(resources.getStringArray(R.array.states).indexOf(state))
                    binding!!.addEditAddressZipEditInput.setText(zipCodeResult)
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("AddressActionCancelled", "User cancelled action!")
            showErrorSnackBar("Action cancelled!", true)
        }
    }

    private fun validateAddressDetails() {
        val fullName = binding!!.addEditAddressFullNameEditInput.text.toString()
        val phone = binding!!.addEditAddressPhoneEditInput.text.toString()
        val address = binding!!.addEditAddressEditInput.text.toString()
        val city = binding!!.addEditAddressCityEditInput.text.toString()
        val zipcode = binding!!.addEditAddressZipEditInput.text.toString()
        val info = binding!!.addEditAddressAdditionalEditInput.text.toString()
        val type = when {
            binding!!.addEditAddressHomeRadioButton.isChecked -> {
                "Home"
            }
            binding!!.addEditAddressOfficeRadioButton.isChecked -> {
                "Office"
            }
            binding!!.addEditAddressOtherRadioButton.isChecked -> {
                "Other"
            }
            else -> {
                ""
            }
        }
        val otherDetails = binding!!.addEditAddressOtherDetailsEditInput.text.toString()
        val default = when {
            binding!!.addEditDefaultCheckBox.isChecked -> {
                true
            }
            else -> {
                false
            }
        }
        when {
            fullName.isEmpty() -> {
                showError(binding!!.addEditAddressFullNameInput, "Please enter your full name!")
            }
            phone.isEmpty() || phone.length != 14 -> {
                hideError(binding!!.addEditAddressFullNameInput)
                showError(binding!!.addEditAddressPhoneInput, "Please enter your phone number!")
            }
            address.isEmpty() -> {
                hideError(binding!!.addEditAddressPhoneInput)
                showError(binding!!.addEditAddressInput, "Please enter your street address!")
            }
            city.isEmpty() -> {
                hideError(binding!!.addEditAddressPhoneInput)
                hideError(binding!!.addEditAddressInput)
                showError(binding!!.addEditAddressCityInput, "Please enter the city you live in!")
            }
            stateSelected.isEmpty() -> {
                hideError(binding!!.addEditAddressPhoneInput)
                hideError(binding!!.addEditAddressCityInput)
                showErrorSnackBar("Please select the state you live in!", true)
            }
            zipcode.isEmpty() -> {
                hideError(binding!!.addEditAddressPhoneInput)
                showError(binding!!.addEditAddressZipInput, "Please enter the zipcode you live in!")
            }
            type.isEmpty() -> {
                hideError(binding!!.addEditAddressPhoneInput)
                hideError(binding!!.addEditAddressZipInput)
                showErrorSnackBar("Please select an address type!", true)
            }
            type == "Other" && otherDetails.isEmpty() -> {
                showError(binding!!.addEditAddressOtherDetailsInput, "Please enter the additional details for type!")
            }
            else -> {
                hideError(binding!!.addEditAddressOtherDetailsInput)
                checkingForDefaultAddress(fullName, phone, address, city, stateSelected, zipcode, lat, long, info, type, otherDetails, default)
            }
        }
    }

    private fun checkingForDefaultAddress(
        fullName: String,
        phone: String,
        address: String,
        city: String,
        state: String,
        zipcode: String,
        lat: String,
        long: String,
        info: String,
        type: String,
        otherDetails: String,
        default: Boolean
    ) {
        if (default) {
            fireStoreClass.checkAddressListItemsForDefault(this@AddEditAddressActivity, fullName, phone, address, city, state, zipcode, lat, long, info, type, otherDetails, default)
        } else {
            savingAddressInfoToFirebase(fullName, phone, address, city, state, zipcode, lat, long, info, type, otherDetails, default)
        }
    }

    private fun savingAddressInfoToFirebase(
        fullName: String,
        phone: String,
        address: String,
        city: String,
        state: String,
        zipcode: String,
        lat: String,
        long: String,
        additionalInfo: String,
        type: String,
        otherDetails: String,
        default: Boolean
    ) {
        if (intent.hasExtra(Constants.ADDRESS_DETAILS)) {
            showProgressDialog("Updating address...")

            val addressModel = Address(
                fireStoreClass.getCurrentUserID(),
                fullName,
                phone,
                address,
                city,
                state,
                zipcode,
                lat,
                long,
                type,
                additionalInfo,
                otherDetails,
                default.toString(),
                addressDetails.id
            )
            fireStoreClass.updateAddress(this@AddEditAddressActivity, addressModel, addressDetails.id)
        } else {
            showProgressDialog("Saving address...")
            val addressModel = Address(
                fireStoreClass.getCurrentUserID(),
                fullName,
                phone,
                address,
                city,
                state,
                zipcode,
                lat,
                long,
                type,
                additionalInfo,
                otherDetails,
                default.toString()
            )
            fireStoreClass.addAddress(this@AddEditAddressActivity, addressModel)
        }
    }

    private fun setUpToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.addEditAddressToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_white)
        if (intent.hasExtra(Constants.ADDRESS_DETAILS)) {
            supportActionBar?.title = "Editing ${addressDetails.city} Address"
        } else {
            supportActionBar?.title = "Add Address"
        }

        toolbar.setTitleTextColor(Color.WHITE)
        supportActionBar?.setBackgroundDrawable(ContextCompat.getDrawable(this@AddEditAddressActivity, R.drawable.gradient_background))
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"))
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun addStateSpinner() {
        val stateAdapter = ArrayAdapter(this@AddEditAddressActivity, R.layout.state_dropdown_item, resources.getStringArray(R.array.states))
        binding!!.addEditStateSpinner.adapter = stateAdapter

        binding!!.addEditStateSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                stateSelected = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    fun addEditAddressSuccess(city: String) {
        hideProgressDialog()
        showErrorSnackBar("Your $city address was successfully added!", false)
        setResult(RESULT_OK)
        Handler(Looper.getMainLooper()).postDelayed({
            finish()
        }, 1000)
    }

    fun updateAddressSuccess() {
        hideProgressDialog()
        showErrorSnackBar("Your address was successfully updated!", false)
        setResult(RESULT_OK)
        Handler(Looper.getMainLooper()).postDelayed({
            finish()
        }, 1000)
    }

    fun defaultChangeSuccess(
        fullName: String,
        phone: String,
        address: String,
        city: String,
        state: String,
        zipcode: String,
        lat: String,
        long: String,
        info: String,
        type: String,
        otherDetails: String,
        default: Boolean) {
        savingAddressInfoToFirebase(fullName, phone, address, city, state, zipcode, lat, long, info, type, otherDetails, default)
    }
}
