package com.macode.stopnshop.view.activities

import android.graphics.Color

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.ActivitySoldProductDetailBinding
import com.macode.stopnshop.model.SoldProduct
import com.macode.stopnshop.utilities.Constants

class SoldProductDetailActivity : BaseActivity() {

    private var binding: ActivitySoldProductDetailBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySoldProductDetailBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        if (intent.hasExtra(Constants.SOLD_PRODUCT_DETAILS)) {
            soldProductDetails = intent.getParcelableExtra(Constants.SOLD_PRODUCT_DETAILS)!!
        }

        setUpToolbar()

        establishSoldProductDetails(soldProductDetails)
    }

    private fun setUpToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.soldProductDetailToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_white)
        supportActionBar?.title = soldProductDetails.title
        toolbar.setTitleTextColor(Color.WHITE)
        supportActionBar?.setBackgroundDrawable(ContextCompat.getDrawable(this@SoldProductDetailActivity, R.drawable.gradient_background))
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"))
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun establishSoldProductDetails(soldProductDetails: SoldProduct) {
        val orderNumber = soldProductDetails.orderID.replace("Order#: ", "")
        binding!!.orderNumber.text = orderNumber
        val dateOrderPlaced = soldProductDetails.orderDate.substring(0, soldProductDetails.orderDate.indexOf(" "))
        binding!!.orderDateNumber.text = dateOrderPlaced

        loadUserImage(this@SoldProductDetailActivity, soldProductDetails.image, binding!!.singleSoldImage)
        binding!!.soldProduct.text = soldProductDetails.title
        "$${soldProductDetails.price}".also { binding!!.soldProductPrice.text = it }
        binding!!.soldProductDesiredAmount.text = soldProductDetails.soldQuantity

        "Type: ${soldProductDetails.address.type}".also { binding!!.shippingAddressType.text = it }
        binding!!.shippingAddressFullName.text = soldProductDetails.address.name
        val fullAddress = "${soldProductDetails.address.address} ${soldProductDetails.address.city}, ${soldProductDetails.address.state}, ${soldProductDetails.address.zipcode}"
        binding!!.shippingAddress.text = fullAddress
        binding!!.shippingAddressPhone.text = soldProductDetails.address.phone
        if (soldProductDetails.address.additionalInfo.isNotEmpty()) {
            binding!!.shippingAddressAdditionalInfo.visibility = View.VISIBLE
            binding!!.shippingAddressAdditionalInfo.text = soldProductDetails.address.additionalInfo
        } else {
            binding!!.shippingAddressAdditionalInfo.visibility = View.GONE
        }
        if (soldProductDetails.address.otherDetails.isNotEmpty()) {
            binding!!.shippingAddressOtherDetails.visibility = View.VISIBLE
            binding!!.shippingAddressOtherDetails.text = soldProductDetails.address.otherDetails
        } else {
            binding!!.shippingAddressOtherDetails.visibility = View.GONE
        }

        binding!!.paymentMethodName.text = soldProductDetails.payment.cardName
        val number = soldProductDetails.payment.cardNumber
        val cardNumberEnding = number.substring(number.length - 4)
        "Credit card ending in ****${cardNumberEnding}".also { binding!!.paymentMethodEndingNumber.text = it }

        if (fullAddress.length > 23) {
            val cutFullAddress = fullAddress.substring(0, 23)
            "$cutFullAddress...".also { binding!!.shippingToValue.text = it }
        } else {
            binding!!.shippingToValue.text = fullAddress
        }

        getSoldProductCalculations()
    }

    private fun getSoldProductCalculations() {
        subtotal = String.format("%.2f", 0.00).toDouble()
        subtotal = soldProductDetails.price.toDouble() * soldProductDetails.soldQuantity.toDouble()
        checkCentsDecimalPlacement(subtotal, binding!!.subtotalNumber)

        val taxTotalNumber: Double = subtotal * (.104)
        waTaxTotal = String.format("%.2f", taxTotalNumber).toDouble()
        checkCentsDecimalPlacement(waTaxTotal, binding!!.waSalesTaxNumber)

        val shippingTotalNumber: Double = calculateShippingTotal(soldProductDetails.address.latitude.toDouble(), soldProductDetails.address.longitude.toDouble())
        shippingTotal = String.format("%.2f", shippingTotalNumber).toDouble()
        when (shippingTotalNumber) {
            in 0.0..2.0 -> {
                shippingTotal = 0.00
            }
            in 2.0..4.0 -> {
                shippingTotal = 5.00
            }
            in 4.0..6.0 -> {
                shippingTotal = 10.00
            }
            in 6.0..8.0 -> {
                shippingTotal = 15.00
            }
            in 8.0..10.0 -> {
                shippingTotal = 20.00
            }
            else -> {
                shippingTotal = 25.00
            }
        }

        checkCentsDecimalPlacement(shippingTotal, binding!!.shippingNumber)

        val totalAmountNumber: Double = subtotal + waTaxTotal + shippingTotal
        totalAmount = String.format("%.2f", totalAmountNumber).toDouble()
        checkCentsDecimalPlacement(totalAmount, binding!!.totalAmountNumber)
    }
}