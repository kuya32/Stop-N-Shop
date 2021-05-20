package com.macode.stopnshop.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.macode.stopnshop.databinding.ActivityPaymentListBinding

class PaymentListActivity : BaseActivity() {

    private var binding: ActivityPaymentListBinding? = null
    private var selectedPaymentBoolean: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentListBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
    }
}