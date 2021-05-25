package com.macode.stopnshop.view.adapters

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.EmailAuthProvider
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.SensitiveInfoDialogBinding
import com.macode.stopnshop.databinding.SingleAddressListItemBinding
import com.macode.stopnshop.firebase.FireStoreClass
import com.macode.stopnshop.model.Address
import com.macode.stopnshop.utilities.Constants
import com.macode.stopnshop.view.activities.AddEditAddressActivity
import com.macode.stopnshop.view.activities.AddressListActivity

class AddressListAdapter(private val context: Context, private val list: ArrayList<Address>, private val selectAddressBoolean: Boolean, private val defaultAddressBoolean: Boolean): RecyclerView.Adapter<AddressListAdapter.ViewHolder>() {

    private val fireStoreClass: FireStoreClass = FireStoreClass()

    inner class ViewHolder(val binding: SingleAddressListItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SingleAddressListItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val address = list[position]
        with(holder) {
            binding.singleAddressFullName.text = address.name
            binding.singleAddressStreet.text = address.address
            "${address.city}, ${address.state} ${address.zipcode}".also { binding.singleAddressCityStateZip.text = it }
            "Phone: ${address.phone}".also { binding.singleAddressPhone.text = it }
            binding.singleAddressType.text = address.type

            if (address.default == "true") {
                binding.singleAddressDefault.visibility = View.VISIBLE
            } else {
                binding.singleAddressDefault.visibility = View.GONE
            }

            if (selectAddressBoolean && !defaultAddressBoolean) {
                itemView.setOnClickListener {
                    showAlertDialogToAssignDefaultAddress(address)
                }
            } else if (selectAddressBoolean && defaultAddressBoolean) {
                itemView.setOnClickListener {
                    val activity = context as AddressListActivity
                    activity.defaultAddressSuccess(address, true)
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

    @RequiresApi(Build.VERSION_CODES.P)
    private fun showAlertDialogToAssignDefaultAddress(addressItem: Address) {
        val builder = AlertDialog.Builder(context)
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.alert_dialog_title, null)
        builder.setCustomTitle(view)
        builder.setMessage("We notice you don't have a default address established. Would you like to set this ${addressItem.city} address as your default?")
        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            dialogInterface.dismiss()
            fireStoreClass.setDefaultAddress(context as AddressListActivity, addressItem)
        }
        builder.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss()
            val activity = context as AddressListActivity
            activity.defaultAddressSuccess(addressItem, false)
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
        val messageView: TextView = alertDialog.requireViewById(android.R.id.message)
        messageView.gravity = Gravity.CENTER
        val positiveButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
        val negativeButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)
        positiveButton.setTextColor(Color.BLACK)
        negativeButton.setTextColor(Color.BLACK)
        val positiveButtonLinearLayout: LinearLayout.LayoutParams = positiveButton.layoutParams as LinearLayout.LayoutParams
        positiveButtonLinearLayout.weight = 10.0f
        positiveButton.layoutParams = positiveButtonLinearLayout
        negativeButton.layoutParams = positiveButtonLinearLayout
    }
}