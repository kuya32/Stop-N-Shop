package com.macode.stopnshop.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.macode.stopnshop.databinding.SingleDashboardListItemBinding
import com.macode.stopnshop.model.Product

class DashboardListAdapter(private val context: Context, private val list: ArrayList<Product>): RecyclerView.Adapter<DashboardListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: SingleDashboardListItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SingleDashboardListItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = list[position]
        with(holder) {
            Glide
                .with(context)
                .load(product.image)
                .into(binding.singleDashboardImage)
            binding.singleDashboardTitle.text = product.title
            "$${product.price}".also { binding.singleDashboardPrice.text = it }
            "Items Left: ${product.stockQuantity}".also { binding.singleDashboardQuantity.text = it }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}