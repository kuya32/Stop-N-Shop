package com.macode.stopnshop.view.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.macode.stopnshop.databinding.SingleAddressListItemBinding
import com.macode.stopnshop.firebase.FireStoreClass
import com.macode.stopnshop.model.Address
import com.macode.stopnshop.utilities.Constants
import com.macode.stopnshop.view.activities.AddEditAddressActivity

class AddressListAdapter(private val context: Context, private val list: ArrayList<Address>, private val selectAddressBoolean: Boolean): RecyclerView.Adapter<AddressListAdapter.ViewHolder>() {

    private val fireStoreClass: FireStoreClass = FireStoreClass()

    inner class ViewHolder(val binding: SingleAddressListItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SingleAddressListItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val address = list[position]
        with(holder) {
            binding.singleAddressFullName.text = address.name
            binding.singleAddressStreet.text = address.address
            "${address.city}, ${address.state} ${address.zipcode}".also { binding.singleAddressCityStateZip.text = it }
            "Phone: ${address.phone}".also { binding.singleAddressPhone.text = it }
            binding.singleAddressType.text = address.type

            if (selectAddressBoolean) {
                itemView.setOnClickListener {

                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun notifyEditItem(activity: Activity, position: Int, requestCode: Int) {
        val intent = Intent(context, AddEditAddressActivity::class.java)
        intent.putExtra(Constants.ADDRESS_DETAILS, list[position])
        activity.startActivityForResult(intent, requestCode)
        notifyItemChanged(position)
    }
}