package com.macode.stopnshop.view.activities

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.ActivityAddressListBinding
import com.macode.stopnshop.model.Address

class AddressListActivity : BaseActivity() {

    private var binding: ActivityAddressListBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressListBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        setUpToolbar()

        binding!!.addAddressButton.setOnClickListener {
            val intent = Intent(this@AddressListActivity, AddEditAddressActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        getAddressList()
    }

    private fun setUpToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.addressListToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_white)
        supportActionBar?.title = "My Address List"
        toolbar.setTitleTextColor(Color.WHITE)
        supportActionBar?.setBackgroundDrawable(ContextCompat.getDrawable(this@AddressListActivity, R.drawable.gradient_background))
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"))
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun getAddressList() {
        showProgressDialog("Retrieving address list...")
        fireStoreClass.getAddressList(this@AddressListActivity)
    }

    fun addressListRetrievalSuccess(addressList: ArrayList<Address>) {
        hideProgressDialog()
    }
}