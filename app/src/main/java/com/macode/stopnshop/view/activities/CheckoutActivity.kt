package com.macode.stopnshop.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.ActivityCheckoutBinding
import com.macode.stopnshop.model.Address

class CheckoutActivity : BaseActivity() {

    private var binding: ActivityCheckoutBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding!!.root)


    }

    fun chooseAddressForCheckout() {

    }

    fun establishAddressForCheckout(addressItem: Address) {
        
    }

//    // TODO: Figure out a way to calculate shipping price depending on the user's address
//    val shippingTotal: Double = String.format("%.2f", 0.00).toDouble()
//    checkCentsDecimalPlacement(shippingTotal, binding!!.shippingNumber)
}