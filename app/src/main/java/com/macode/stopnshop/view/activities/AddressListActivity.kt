package com.macode.stopnshop.view.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.ActivityAddressListBinding
import com.macode.stopnshop.model.Address
import com.macode.stopnshop.utilities.Constants
import com.macode.stopnshop.utilities.SwipeToDeleteCallback
import com.macode.stopnshop.utilities.SwipeToEditCallback
import com.macode.stopnshop.view.adapters.AddressListAdapter

class AddressListActivity : BaseActivity() {

    private var binding: ActivityAddressListBinding? = null
    private var selectAddressBoolean: Boolean = false
    private var defaultAddressBoolean: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressListBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        if (intent.hasExtra(Constants.SELECT_ADDRESS_BOOLEAN)) {
            selectAddressBoolean = intent.getBooleanExtra(Constants.SELECT_ADDRESS_BOOLEAN, false)
        }

        if (intent.hasExtra(Constants.DEFAULT_ADDRESS_BOOLEAN)) {
            defaultAddressBoolean = intent.getBooleanExtra(Constants.DEFAULT_ADDRESS_BOOLEAN, true)
        }

        setUpToolbar()
        getAddressList()

        binding!!.addAddressButton.setOnClickListener {
            val intent = Intent(this@AddressListActivity, AddEditAddressActivity::class.java)
            startActivityForResult(intent, ADD_EDIT_ADDRESS_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            getAddressList()
        }
    }

    private fun setUpToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.addressListToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_white)
        if (selectAddressBoolean) {
            supportActionBar?.title = "Select Address"
        } else {
            supportActionBar?.title = "My Address List"
        }

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

        addressItemList = addressList

        if (addressItemList.size > 0) {
            binding!!.noAddressesFound.visibility = View.GONE
            binding!!.addressListRecyclerView.visibility = View.VISIBLE
            binding!!.addressListRecyclerView.layoutManager = LinearLayoutManager(this@AddressListActivity)
            binding!!.addressListRecyclerView.setHasFixedSize(true)
            val addressAdapter = AddressListAdapter(this@AddressListActivity, addressList, selectAddressBoolean, defaultAddressBoolean)
            binding!!.addressListRecyclerView.adapter = addressAdapter
        } else {
            binding!!.noAddressesFound.visibility = View.VISIBLE
            binding!!.addressListRecyclerView.visibility = View.GONE
        }

        if (!selectAddressBoolean) {
            val editSwipeHandler = object: SwipeToEditCallback(this@AddressListActivity) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val addressAdapter = binding!!.addressListRecyclerView.adapter as AddressListAdapter
                    addressAdapter.notifyEditItem(this@AddressListActivity, viewHolder.adapterPosition, ADD_EDIT_ADDRESS_REQUEST_CODE)
                }
            }

            val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
            editItemTouchHelper.attachToRecyclerView(binding!!.addressListRecyclerView)

            val deleteSwipeHandler = object : SwipeToDeleteCallback(this@AddressListActivity) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    showProgressDialog("Deleting address...")
                    fireStoreClass.deleteAddress(this@AddressListActivity, addressItemList[viewHolder.adapterPosition].id, addressItemList[viewHolder.adapterPosition].city)
                }

            }

            val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
            deleteItemTouchHelper.attachToRecyclerView(binding!!.addressListRecyclerView)
        }
    }

    fun addressDeleteSuccess(city: String) {
        hideProgressDialog()
        showErrorSnackBar("$city address successfully deleted!", false)
        getAddressList()
    }

    fun defaultAddressSuccess(addressItem: Address, isDefaultSet: Boolean) {
        val intent = Intent()
        intent.putExtra(Constants.SELECTED_ADDRESS_DETAILS, addressItem)
        intent.putExtra(Constants.DEFAULT_ADDRESS_BOOLEAN, isDefaultSet)
        setResult(RESULT_OK, intent)
        finish()
    }
}