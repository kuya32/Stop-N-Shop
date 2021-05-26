package com.macode.stopnshop.view.activities

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.ActivityOrderDetailBinding
import com.macode.stopnshop.model.Order
import com.macode.stopnshop.utilities.Constants
import com.macode.stopnshop.view.adapters.CartListAdapter
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class OrderDetailActivity : BaseActivity() {

    private var binding: ActivityOrderDetailBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        if (intent.hasExtra(Constants.ORDER_DETAILS)) {
            orderDetails = intent.getParcelableExtra(Constants.ORDER_DETAILS)!!
        }

        setUpToolbar()

        establishOrderDetails(orderDetails)
    }

    private fun setUpToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.orderDetailToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_white)
        supportActionBar?.title = orderDetails.title
        toolbar.setTitleTextColor(Color.WHITE)
        supportActionBar?.setBackgroundDrawable(ContextCompat.getDrawable(this@OrderDetailActivity, R.drawable.gradient_background))
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"))
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun establishOrderDetails(orderDetails: Order) {
        val orderNumber = orderDetails.title.replace("Order#: ", "")
        binding!!.orderNumber.text = orderNumber
        val dateOrderPlaced = orderDetails.dateOrderPlaced.substring(0, orderDetails.dateOrderPlaced.indexOf(" "))
        binding!!.orderDateNumber.text = dateOrderPlaced

        val sdf = SimpleDateFormat("MM/dd/yyyy hh:mm:ss", Locale.getDefault())
        val date: Date? = sdf.parse(orderDetails.dateOrderPlaced)
        val dateOrderPlacedInMilli = date?.time
        val differInMilliSeconds: Long = System.currentTimeMillis() - dateOrderPlacedInMilli!!
        when (TimeUnit.MILLISECONDS.toHours(differInMilliSeconds)) {
            in 0..2 -> {
                binding!!.orderStatusIndicator.text = resources.getString(R.string.pending)
                binding!!.orderStatusIndicator.setTextColor(ContextCompat.getColor(this@OrderDetailActivity, R.color.red))
            }
            in 2..5 -> {
                binding!!.orderStatusIndicator.text = resources.getString(R.string.inProgress)
                binding!!.orderStatusIndicator.setTextColor(ContextCompat.getColor(this@OrderDetailActivity, R.color.primaryLightColor))
            }
            else -> {
                binding!!.orderStatusIndicator.text = resources.getString(R.string.delivered)
                binding!!.orderStatusIndicator.setTextColor(ContextCompat.getColor(this@OrderDetailActivity, R.color.green))
            }
        }

        binding!!.productItemsRV.layoutManager = LinearLayoutManager(this@OrderDetailActivity)
        binding!!.productItemsRV.setHasFixedSize(true)
        val cartListAdapter = CartListAdapter(this@OrderDetailActivity, orderDetails.items, false)
        binding!!.productItemsRV.adapter = cartListAdapter

        "Type: ${orderDetails.address.type}".also { binding!!.shippingAddressType.text = it }
        binding!!.shippingAddressFullName.text = orderDetails.address.name
        val fullAddress = "${orderDetails.address.address} ${orderDetails.address.city}, ${orderDetails.address.state}, ${orderDetails.address.zipcode}"
        binding!!.shippingAddress.text = fullAddress
        binding!!.shippingAddressPhone.text = orderDetails.address.phone
        if (orderDetails.address.additionalInfo.isNotEmpty()) {
            binding!!.shippingAddressAdditionalInfo.visibility = View.VISIBLE
            binding!!.shippingAddressAdditionalInfo.text = orderDetails.address.additionalInfo
        } else {
            binding!!.shippingAddressAdditionalInfo.visibility = View.GONE
        }
        if (orderDetails.address.otherDetails.isNotEmpty()) {
            binding!!.shippingAddressOtherDetails.visibility = View.VISIBLE
            binding!!.shippingAddressOtherDetails.text = orderDetails.address.otherDetails
        } else {
            binding!!.shippingAddressOtherDetails.visibility = View.GONE
        }

        binding!!.paymentMethodName.text = orderDetails.payment.cardName
        val number = orderDetails.payment.cardNumber
        val cardNumberEnding = number.substring(number.length - 4)
        "Credit card ending in ****${cardNumberEnding}".also { binding!!.paymentMethodEndingNumber.text = it }

        if (fullAddress.length > 23) {
            val cutFullAddress = fullAddress.substring(0, 23)
            "$cutFullAddress...".also { binding!!.shippingToValue.text = it }
        } else {
            binding!!.shippingToValue.text = fullAddress
        }

        "$${orderDetails.subTotalAmount}".also { binding!!.itemsNumber.text = it }
        "$${orderDetails.waTaxAmount}".also { binding!!.waSalesTaxNumber.text = it }
        "$${orderDetails.shippingAmount}".also { binding!!.shippingNumber.text = it }
        "$${orderDetails.totalAmount}".also { binding!!.totalAmountNumber.text = it }
    }
}