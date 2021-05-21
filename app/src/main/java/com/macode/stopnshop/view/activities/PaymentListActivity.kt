package com.macode.stopnshop.view.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.ActivityPaymentListBinding
import com.macode.stopnshop.model.Payment
import com.macode.stopnshop.utilities.Constants
import com.macode.stopnshop.utilities.SwipeToEditCallback
import com.macode.stopnshop.view.adapters.PaymentListAdapter

class PaymentListActivity : BaseActivity() {

    private var binding: ActivityPaymentListBinding? = null
    private var selectedPaymentBoolean: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentListBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        if (intent.hasExtra(Constants.SELECT_PAYMENT_BOOLEAN)) {
            selectedPaymentBoolean = intent.getBooleanExtra(Constants.SELECT_PAYMENT_BOOLEAN, false)
        }

        setUpToolbar()
        getPaymentList()

        binding!!.addPaymentButton.setOnClickListener {
            val intent = Intent(this@PaymentListActivity, AddEditPaymentActivity::class.java)
            startActivityForResult(intent, ADD_EDIT_PAYMENT_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            getPaymentList()
        }
    }

    private fun setUpToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.paymentListToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_white)
        if (selectedPaymentBoolean) {
            supportActionBar?.title = "Select a Payment Method"
        } else {
            supportActionBar?.title = "My Payment Method List"
        }

        toolbar.setTitleTextColor(Color.WHITE)
        supportActionBar?.setBackgroundDrawable(ContextCompat.getDrawable(this@PaymentListActivity, R.drawable.gradient_background))
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"))
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun getPaymentList() {
        showProgressDialog("Retrieving payment method list...")
        fireStoreClass.getPaymentList(this@PaymentListActivity)
    }

    fun paymentListRetrievalSuccess(paymentList: ArrayList<Payment>) {
        hideProgressDialog()

        paymentItemList = paymentList

        if (paymentItemList.size > 0) {
            binding!!.noPaymentsFound.visibility = View.GONE
            binding!!.paymentListRecyclerView.visibility = View.VISIBLE
            binding!!.paymentListRecyclerView.layoutManager = LinearLayoutManager(this@PaymentListActivity)
            binding!!.paymentListRecyclerView.setHasFixedSize(true)
            val paymentAdapter = PaymentListAdapter(this@PaymentListActivity, paymentItemList, selectedPaymentBoolean)
            binding!!.paymentListRecyclerView.adapter = paymentAdapter
        } else {
            binding!!.noPaymentsFound.visibility = View.VISIBLE
            binding!!.paymentListRecyclerView.visibility = View.GONE
        }

//        if (!selectedPaymentBoolean) {
//            val editSwipeHandler = object: SwipeToEditCallback(this@PaymentListActivity)
//        }
    }
}