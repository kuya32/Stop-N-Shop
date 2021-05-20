package com.macode.stopnshop.view.activities

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.ActivityAddressListBinding
import com.macode.stopnshop.model.Address
import com.macode.stopnshop.utilities.SwipeToDeleteCallback
import com.macode.stopnshop.utilities.SwipeToEditCallback
import com.macode.stopnshop.view.adapters.AddressListAdapter

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

        if (addressList.size > 0) {
            binding!!.noAddressesFound.visibility = View.GONE
            binding!!.addressListRecyclerView.visibility = View.VISIBLE
            binding!!.addressListRecyclerView.layoutManager = LinearLayoutManager(this@AddressListActivity)
            binding!!.addressListRecyclerView.setHasFixedSize(true)
            val addressAdapter = AddressListAdapter(this@AddressListActivity, addressList)
            binding!!.addressListRecyclerView.adapter = addressAdapter
        } else {
            binding!!.noAddressesFound.visibility = View.VISIBLE
            binding!!.addressListRecyclerView.visibility = View.GONE
        }

        val editSwipeHandler = object: SwipeToEditCallback(this@AddressListActivity) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val addressAdapter = binding!!.addressListRecyclerView.adapter as AddressListAdapter
                addressAdapter.notifyEditItem(this@AddressListActivity, viewHolder.adapterPosition, EDIT_ADDRESS_REQUEST_CODE)
            }
        }

        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(binding!!.addressListRecyclerView)

        val deleteSwipeHandler = object : SwipeToDeleteCallback(this@AddressListActivity) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val addressAdapter = binding!!.addressListRecyclerView.adapter as AddressListAdapter
                addressAdapter.removeAt(viewHolder.adapterPosition)
            }

        }

        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(binding!!.addressListRecyclerView)
    }


}