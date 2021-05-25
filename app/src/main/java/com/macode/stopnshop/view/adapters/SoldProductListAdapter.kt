package com.macode.stopnshop.view.adapters

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.SingleSoldProductListItemBinding
import com.macode.stopnshop.model.SoldProduct
import com.macode.stopnshop.utilities.Constants
import com.macode.stopnshop.view.activities.SoldProductDetailActivity
import com.macode.stopnshop.view.fragments.SoldProductsFragment

class SoldProductListAdapter(private val context: Context, private val list: ArrayList<SoldProduct>, private val fragment: SoldProductsFragment): RecyclerView.Adapter<SoldProductListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: SingleSoldProductListItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SingleSoldProductListItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val soldProduct = list[position]
        with(holder) {
            Glide
                .with(context)
                .load(soldProduct.image)
                .centerCrop()
                .placeholder(R.drawable.profile_place_holder)
                .into(binding.singleSoldImage)

            binding.soldProduct.text = soldProduct.title
            "$${soldProduct.price}".also { binding.soldProductPrice.text = it }

            itemView.setOnClickListener {
                val intent = Intent(context, SoldProductDetailActivity::class.java)
                intent.putExtra(Constants.SOLD_PRODUCT_DETAILS, soldProduct)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}