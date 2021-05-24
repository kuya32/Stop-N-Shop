package com.macode.stopnshop.view.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.SinglePaymentListItemBinding
import com.macode.stopnshop.firebase.FireStoreClass
import com.macode.stopnshop.model.Address
import com.macode.stopnshop.model.Payment
import com.macode.stopnshop.utilities.Constants
import com.macode.stopnshop.view.activities.AddEditAddressActivity
import com.macode.stopnshop.view.activities.AddEditPaymentActivity
import com.macode.stopnshop.view.activities.AddressListActivity
import com.macode.stopnshop.view.activities.PaymentListActivity

class PaymentListAdapter(private val context: Context, private val list: ArrayList<Payment>, private val selectPaymentBoolean: Boolean, private val defaultPaymentBoolean: Boolean): RecyclerView.Adapter<PaymentListAdapter.ViewHolder>(){

    private val fireStoreClass: FireStoreClass = FireStoreClass()

    inner class ViewHolder(val binding: SinglePaymentListItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SinglePaymentListItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val payment = list[position]
        with(holder) {
            val number = payment.cardNumber
            val cardNumberEnding = number.substring(number.length - 4)
            "Credit Card ending in ****$cardNumberEnding".also { binding.singlePaymentEndingNumber.text = it }
            binding.singlePaymentCardName.text = payment.cardName
            "Exp: ${payment.expirationMonth}/${payment.expirationYear}".also { binding.singlePaymentExpirationDate.text = it }

            if (payment.default == "true") {
                binding.singlePaymentDefault.visibility = View.VISIBLE
            } else {
                binding.singlePaymentDefault.visibility = View.GONE
            }

            if (selectPaymentBoolean && !defaultPaymentBoolean) {
                itemView.setOnClickListener {

                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun notifyEditItem(activity: Activity, position: Int, requestCode: Int) {
        val intent = Intent(context, AddEditPaymentActivity::class.java)
        intent.putExtra(Constants.PAYMENT_DETAILS, list[position])
        activity.startActivityForResult(intent, requestCode)
        notifyItemChanged(position)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun showAlertDialogToAssignDefaultPayment(paymentItem: Payment) {
        val builder = AlertDialog.Builder(context)
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.alert_dialog_title, null)
        builder.setCustomTitle(view)
        builder.setMessage("We notice you don't have a default payment established. Would you like to set this payment method as your default?")
        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            dialogInterface.dismiss()
            fireStoreClass.setDefaultPayment(context as PaymentListActivity, paymentItem)
        }
        builder.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss()
            val activity = context as AddressListActivity
            activity.defaultPaymentSuccess(paymentItem, false)
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