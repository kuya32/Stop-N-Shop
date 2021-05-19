package com.macode.stopnshop.view.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.size
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.ActivityAddEditAddressBinding
import java.lang.Exception

class AddEditAddressActivity : BaseActivity() {

    private var binding: ActivityAddEditAddressBinding? = null
    private var stateSelected: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditAddressBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        setUpToolbar()

        if (!Places.isInitialized()) {
            Places.initialize(this@AddEditAddressActivity, resources.getString(R.string.google_maps_key))
        }

        addStateSpinner()

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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PLACE_AUTOCOMPLETE_REQUEST_CODE -> {
                    val place: Place = Autocomplete.getPlaceFromIntent(data!!)
                    binding!!.addEditAddressEditInput.setText(place.name.toString())
                    val regex = "([^,]+), ([A-Z]{2,})".toRegex()
                    val zipRegex ="\\b\\d{5}\\b".toRegex()
                    val matchResult = regex.find(place.address.toString())?.value.toString()
                    val zipCodeResult = zipRegex.find(place.address.toString())?.value.toString()
                    val city = matchResult.substring(1, matchResult.indexOf(","))
                    val state = matchResult.substring(matchResult.indexOf(",") + 2)
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
        TODO("Not yet implemented")
    }

    private fun setUpToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.addEditAddressToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_white)
        supportActionBar?.title = "Add Address"
        toolbar.setTitleTextColor(Color.WHITE)
        supportActionBar?.setBackgroundDrawable(ContextCompat.getDrawable(this@AddEditAddressActivity, R.drawable.gradient_background))
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"))
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun addStateSpinner() {
        val stateAdapter = ArrayAdapter<String>(this@AddEditAddressActivity, R.layout.state_dropdown_item, resources.getStringArray(R.array.states))
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
}
