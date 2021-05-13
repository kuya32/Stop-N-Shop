package com.macode.stopnshop.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.macode.stopnshop.databinding.SingleProductListItemBinding
import com.macode.stopnshop.model.Product

open class MyProductsListAdapter(private val context: Context, private val list: ArrayList<Product>): RecyclerView.Adapter<MyProductsListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: SingleProductListItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SingleProductListItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = list[position]
        with(holder) {
            Glide
                .with(context)
                .load(product.image)
                .into(binding.singleProductImage)
            binding.singleProductTitle.text = product.title
            "$${product.price}".also { binding.singleProductPrice.text = it }
            "Items Left: ${product.stockQuantity}".also { binding.singleProductQuantity.text = it }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}