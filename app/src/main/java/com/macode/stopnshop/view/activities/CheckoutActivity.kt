package com.macode.stopnshop.view.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.ActivityCheckoutBinding
import com.macode.stopnshop.model.*
import com.macode.stopnshop.utilities.Constants
import com.macode.stopnshop.utilities.SNSTextView
import com.macode.stopnshop.view.adapters.CartListAdapter
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

class CheckoutActivity : BaseActivity(), View.OnClickListener {

    private var binding: ActivityCheckoutBinding? = null
    private var isDefaultAddressSet: Boolean = true
    private var isDefaultPaymentSet: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireStoreClass.retrieveDefaultAddress(this@CheckoutActivity)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        setUpToolbar()

        binding!!.shippingAddressArrow.setOnClickListener(this@CheckoutActivity)
        binding!!.paymentMethodArrow.setOnClickListener(this@CheckoutActivity)
        binding!!.placeYourOrderButton.setOnClickListener(this@CheckoutActivity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            DEFAULT_ADDRESS -> {
                isDefaultAddressSet = data?.getBooleanExtra(Constants.DEFAULT_ADDRESS_BOOLEAN, false)!!
                if (resultCode == Activity.RESULT_OK && isDefaultAddressSet) {
                    establishAddressForCheckout(data.getParcelableExtra(Constants.SELECTED_ADDRESS_DETAILS)!!)
                } else if (resultCode == Activity.RESULT_OK && !isDefaultAddressSet) {
                    establishAddressForCheckout(data.getParcelableExtra(Constants.SELECTED_ADDRESS_DETAILS)!!)
                }
            }
            DEFAULT_PAYMENT -> {
                isDefaultPaymentSet = data?.getBooleanExtra(Constants.DEFAULT_PAYMENT_BOOLEAN, false)!!
                if (resultCode == Activity.RESULT_OK && isDefaultPaymentSet) {
                    establishPaymentForCheckout(data.getParcelableExtra(Constants.SELECTED_PAYMENT_DETAILS)!!)
                } else if (resultCode == Activity.RESULT_OK && !isDefaultPaymentSet) {
                    establishPaymentForCheckout(data.getParcelableExtra(Constants.SELECTED_PAYMENT_DETAILS)!!)
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.shippingAddressArrow -> {
                val intent = Intent(this@CheckoutActivity, AddressListActivity::class.java)
                intent.putExtra(Constants.SELECT_ADDRESS_BOOLEAN, true)
                intent.putExtra(Constants.DEFAULT_ADDRESS_BOOLEAN, isDefaultAddressSet)
                startActivityForResult(intent, DEFAULT_ADDRESS)
            }
            R.id.paymentMethodArrow -> {
                val intent = Intent(this@CheckoutActivity, PaymentListActivity::class.java)
                intent.putExtra(Constants.SELECT_PAYMENT_BOOLEAN, true)
                intent.putExtra(Constants.DEFAULT_PAYMENT_BOOLEAN, isDefaultAddressSet)
                startActivityForResult(intent, DEFAULT_PAYMENT)
            }
            R.id.placeYourOrderButton -> {
                placeTheOrder()
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

    private fun getProductList() {
        showProgressDialog("Retrieving product list...")
        fireStoreClass.getAllProductsList(this@CheckoutActivity)
    }

    private fun getCartItemList() {
        fireStoreClass.getCartList(this@CheckoutActivity)
    }

    private fun getCartPriceCalculations() {
        subtotal = String.format("%.2f", 0.00).toDouble()
        for (item in cartItemsList) {
            val availableQuantity = item.stockQuantity.toInt()
            if (availableQuantity > 0) {
                val price = item.price.toDouble()
                val quantity = item.cartQuantity.toInt()
                subtotal += (price * quantity)
            }
        }
        checkCentsDecimalPlacement(subtotal, binding!!.itemsNumber)

        val taxTotalNumber: Double = subtotal * (.104)
        waTaxTotal = String.format("%.2f", taxTotalNumber).toDouble()
        checkCentsDecimalPlacement(waTaxTotal, binding!!.waSalesTaxNumber)

        val shippingTotalNumber: Double = calculateShippingTotal(addressDetails.latitude.toDouble(), addressDetails.longitude.toDouble())
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

    private fun placeTheOrder() {
        showProgressDialog("Placing your order...")
        orderDetails = Order(
            getDate(),
            fireStoreClass.getCurrentUserID(),
            cartItemsList,
            addressDetails,
            paymentDetails,
            "Order#: ${System.currentTimeMillis()}",
            cartItemsList[0].image,
            binding!!.totalAmountNumber.text.toString().replace("$", ""),
            binding!!.waSalesTaxNumber.text.toString().replace("$", ""),
            binding!!.shippingNumber.text.toString().replace("$", ""),
            binding!!.totalAmountNumber.text.toString().replace("$", "")
        )

        fireStoreClass.placeOrder(this@CheckoutActivity, orderDetails)
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
        } else {
            binding!!.shippingAddressAdditionalInfo.visibility = View.GONE
        }
        if (addressDetails.otherDetails.isNotEmpty()) {
            binding!!.shippingAddressOtherDetails.visibility = View.VISIBLE
            binding!!.shippingAddressOtherDetails.text = addressDetails.otherDetails
        } else {
            binding!!.shippingAddressOtherDetails.visibility = View.GONE
        }
        val fullAddress = "${addressDetails.address} ${addressDetails.city}, ${addressDetails.state}, ${addressDetails.zipcode}"
        if (fullAddress.length > 23) {
            val cutFullAddress = fullAddress.substring(0, 23)
            "$cutFullAddress...".also { binding!!.shippingToValue.text = it }
        } else {
            binding!!.shippingToValue.text = fullAddress
        }

        fireStoreClass.retrieveDefaultPayment(this@CheckoutActivity)
        getProductList()
    }

    fun choosePaymentForCheckout() {
        val intent = Intent(this@CheckoutActivity, PaymentListActivity::class.java)
        intent.putExtra(Constants.SELECT_PAYMENT_BOOLEAN, true)
        startActivityForResult(intent, DEFAULT_PAYMENT)
    }

    fun establishPaymentForCheckout(paymentItem: Payment) {
        paymentDetails = paymentItem
        binding!!.paymentMethodName.text = paymentDetails.cardName
        val number = paymentDetails.cardNumber
        val cardNumberEnding = number.substring(number.length - 4)
        "Credit card ending in ****${cardNumberEnding}".also { binding!!.paymentMethodEndingNumber.text = it }
    }

    fun allProductListSuccess(productsList: ArrayList<Product>) {
        productList = productsList
        getCartItemList()
    }

    fun cartListRetrievalSuccess(cartList: ArrayList<CartItem>) {
        hideProgressDialog()

        cartItemsList = cartList
        for (product in productList) {
            for (cartItem in cartItemsList) {
                if (product.id == cartItem.id) {
                    cartItem.stockQuantity = product.stockQuantity
                }
            }
        }

        binding!!.productItemsRV.layoutManager = LinearLayoutManager(this@CheckoutActivity)
        binding!!.productItemsRV.setHasFixedSize(true)
        val checkoutAdapter = CartListAdapter(this@CheckoutActivity, cartItemsList, false)
        binding!!.productItemsRV.adapter = checkoutAdapter

        getCartPriceCalculations()
    }

    fun orderPlacedSuccess() {
        fireStoreClass.updateProductAndCartDetails(this@CheckoutActivity, cartItemsList, orderDetails)
    }

    fun productAndCartDetailsUpdatedSuccessfully() {
        hideProgressDialog()
        showErrorSnackBar("Your order was placed!", false)
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@CheckoutActivity, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }, 1000)
    }
}