package com.macode.stopnshop.view.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.macode.stopnshop.databinding.SinglePaymentListItemBinding
import com.macode.stopnshop.model.Payment
import com.macode.stopnshop.utilities.Constants
import com.macode.stopnshop.view.activities.AddEditAddressActivity
import com.macode.stopnshop.view.activities.AddEditPaymentActivity

class PaymentListAdapter(private val context: Context, private val list: ArrayList<Payment>, private val selectPaymentBoolean: Boolean): RecyclerView.Adapter<PaymentListAdapter.ViewHolder>(){

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
            "${payment.expirationMonth}/${payment.expirationYear}".also { binding.singlePaymentExpirationDate.text = it }

            if (payment.default == "true") {
                binding.singlePaymentDefault.visibility = View.VISIBLE
            } else {
                binding.singlePaymentDefault.visibility = View.GONE
            }

            if (selectPaymentBoolean) {
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
}