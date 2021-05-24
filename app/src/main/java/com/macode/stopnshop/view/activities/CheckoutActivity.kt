package com.macode.stopnshop.view.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.ActivityCheckoutBinding
import com.macode.stopnshop.model.Address
import com.macode.stopnshop.model.Payment
import com.macode.stopnshop.utilities.Constants
import com.macode.stopnshop.view.adapters.AddressListAdapter

class CheckoutActivity : BaseActivity() {

    private var binding: ActivityCheckoutBinding? = null
    private var isDefaultAddressSet: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireStoreClass.retrieveDefaultAddress(this@CheckoutActivity)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        setUpToolbar()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        isDefaultAddressSet = data?.getBooleanExtra(Constants.SELECT_ADDRESS_BOOLEAN, false)!!
        when (requestCode) {
            DEFAULT_ADDRESS -> {
                if (resultCode == Activity.RESULT_OK && isDefaultAddressSet) {
                    establishAddressForCheckout(data.getParcelableExtra(Constants.ADDRESS_DETAILS)!!)
                } else if (resultCode == Activity.RESULT_OK && !isDefaultAddressSet) {
                    establishAddressForCheckout(data.getParcelableExtra(Constants.ADDRESS_DETAILS)!!)
                }
            }
        }
    }

    private fun setUpToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.checkoutToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_white)
        supportActionBar?.title = "Checkout"
        toolbar.setTitleTextColor(Color.WHITE)
        supportActionBar?.setBackgroundDrawable(ContextCompat.getDrawable(this@CheckoutActivity, R.drawable.gradient_background))
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"))
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun chooseAddressForCheckout() {
        val intent = Intent(this@CheckoutActivity, AddressListActivity::class.java)
        intent.putExtra(Constants.SELECT_ADDRESS_BOOLEAN, true)
        startActivityForResult(intent, DEFAULT_ADDRESS)
    }

    fun establishAddressForCheckout(addressItem: Address) {
        addressDetails = addressItem
        "Type: ${addressDetails.type}".also { binding!!.shippingAddressType.text = it }
        binding!!.shippingAddressFullName.text = addressDetails.name
        "${addressDetails.address} ${addressDetails.city}, ${addressDetails.state}, ${addressDetails.zipcode}".also { binding!!.shippingAddress.text = it }
        binding!!.shippingAddressPhone.text = addressDetails.phone
        if (addressDetails.additionalInfo.isNotEmpty()) {
            binding!!.shippingAddressAdditionalInfo.visibility = View.VISIBLE
            binding!!.shippingAddressAdditionalInfo.text = addressDetails.additionalInfo
        }
        if (addressDetails.otherDetails.isNotEmpty()) {
            binding!!.shippingAddressOtherDetails.visibility = View.VISIBLE
            binding!!.shippingAddressOtherDetails.text = addressDetails.otherDetails
        }
        fireStoreClass.retrieveDefaultPayment(this@CheckoutActivity)
    }

    fun choosePaymentForCheckout() {
        val intent = Intent(this@CheckoutActivity, PaymentListActivity::class.java)
        intent.putExtra(Constants.SELECT_PAYMENT_BOOLEAN, true)
        startActivity(intent)
    }

    fun establishPaymentForCheckout(paymentItem: Payment) {

    }

//    // TODO: Figure out a way to calculate shipping price depending on the user's address
//    val shippingTotal: Double = String.format("%.2f", 0.00).toDouble()
//    checkCentsDecimalPlacement(shippingTotal, binding!!.shippingNumber)
}