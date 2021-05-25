package com.macode.stopnshop.view.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.SingleOrderListItemBinding
import com.macode.stopnshop.model.Order
import com.macode.stopnshop.utilities.Constants
import com.macode.stopnshop.view.fragments.OrdersFragment

class OrderListAdapter(private val context: Context, private val list: ArrayList<Order>, private val fragment: OrdersFragment): RecyclerView.Adapter<OrderListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: SingleOrderListItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SingleOrderListItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = list[position]
        with(holder) {
            Glide
                .with(context)
                .load(order.image)
                .centerCrop()
                .placeholder(R.drawable.profile_place_holder)
                .into(binding.singleOrderImage)

            binding.orderNumber.text = order.title
            binding.orderPrice.text = order.totalAmount

            binding.deleteOrderButton.setOnClickListener {
                fragment.showAlertDialogToDeleteOrder(order.id, order.title)
            }

            itemView.setOnClickListener {
//                val intent = Intent(context, )
//                intent.putExtra(Constants.ORDER_DETAILS, order)
//                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}