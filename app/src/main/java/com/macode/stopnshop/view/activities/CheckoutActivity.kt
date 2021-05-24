package com.macode.stopnshop.view.activities

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.ActivityCheckoutBinding
import com.macode.stopnshop.model.Address
import com.macode.stopnshop.utilities.Constants

class CheckoutActivity : BaseActivity() {

    private var binding: ActivityCheckoutBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        setUpToolbar()
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

    }

    fun establishAddressForCheckout(addressItem: Address) {
        
    }

//    // TODO: Figure out a way to calculate shipping price depending on the user's address
//    val shippingTotal: Double = String.format("%.2f", 0.00).toDouble()
//    checkCentsDecimalPlacement(shippingTotal, binding!!.shippingNumber)
}