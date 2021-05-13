package com.macode.stopnshop.view.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.macode.stopnshop.databinding.SingleProductListItemBinding
import com.macode.stopnshop.model.Product
import com.macode.stopnshop.view.fragments.ProductsFragment

open class MyProductsListAdapter(private val context: Context, private val list: ArrayList<Product>, private val fragment: ProductsFragment): RecyclerView.Adapter<MyProductsListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: SingleProductListItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SingleProductListItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.P)
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

            binding.deleteProductButton.setOnClickListener {
                fragment.showAlertDialogToDeleteProduct(product.id, product.title)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}