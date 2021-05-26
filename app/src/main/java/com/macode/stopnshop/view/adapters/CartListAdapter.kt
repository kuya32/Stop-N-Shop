package com.macode.stopnshop.view.adapters

import android.content.Context
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
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.SingleCartListItemBinding
import com.macode.stopnshop.firebase.FireStoreClass
import com.macode.stopnshop.model.CartItem
import com.macode.stopnshop.utilities.Constants
import com.macode.stopnshop.view.activities.CartListActivity

class CartListAdapter(private val context: Context, private val list: ArrayList<CartItem>, private val updateCartItems: Boolean): RecyclerView.Adapter<CartListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: SingleCartListItemBinding): RecyclerView.ViewHolder(binding.root)

    private val fireStoreClass: FireStoreClass = FireStoreClass()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SingleCartListItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cartItem = list[position]
        with(holder) {
            Glide
                .with(context)
                .load(cartItem.image)
                .into(binding.singleCartImage)
            binding.singleCartTitle.text = cartItem.title
            "$${cartItem.price}".also { binding.singleCartPrice.text = it }
            binding.singleCartDesiredAmount.text = cartItem.cartQuantity

            if (cartItem.cartQuantity == "0") {
                binding.singleCartMinusButton.visibility = View.GONE
                binding.singleCartPlusButton.visibility = View.GONE

                if (updateCartItems) {
                    binding.singleCartDeleteButton.visibility = View.VISIBLE
                } else {
                    binding.singleCartDeleteButton.visibility = View.GONE
                }

                binding.singleCartDesiredAmount.text = context.resources.getString(R.string.outOfStock)
                binding.singleCartDesiredAmount.textSize = context.resources.getDimension(R.dimen._4sdp)
                binding.singleCartDesiredAmount.setTextColor(ContextCompat.getColor(context, R.color.red))
            } else {
                if (updateCartItems) {
                    binding.singleCartMinusButton.visibility = View.VISIBLE
                    binding.singleCartPlusButton.visibility = View.VISIBLE
                    binding.singleCartDeleteButton.visibility = View.VISIBLE
                } else {
                    binding.singleCartMinusButton.visibility = View.GONE
                    binding.singleCartPlusButton.visibility = View.GONE
                    binding.singleCartDeleteButton.visibility = View.GONE
                }
            }

            binding.singleCartDeleteButton.setOnClickListener {
                showAlertDialogToDeleteCartItem(cartItem.id, cartItem.title)
            }

            binding.singleCartMinusButton.setOnClickListener {
                if (cartItem.cartQuantity == "1") {
                    if (context is CartListActivity) {
                        context.showProgressDialog("Deleting Cart Item...")
                        fireStoreClass.deleteCartItem(context, cartItem.id, cartItem.title)
                    }
                } else {
                    val cartQuantity: Int = cartItem.cartQuantity.toInt()
                    val cartItemHashMap = HashMap<String, Any>()

                    cartItemHashMap[Constants.CART_QUANTITY] = (cartQuantity - 1).toString()

                    fireStoreClass.updateMyCart(context, cartItem.id, cartItemHashMap)
                }
            }

            binding.singleCartPlusButton.setOnClickListener {
                val cartQuantity: Int = cartItem.cartQuantity.toInt()
                if (cartQuantity < cartItem.stockQuantity.toInt()) {
                    val itemHashMap = HashMap<String, Any>()
                    itemHashMap[Constants.CART_QUANTITY] = (cartQuantity + 1).toString()

                    fireStoreClass.updateMyCart(context, cartItem.id, itemHashMap)
                } else {
                    if (context is CartListActivity) {
                        context.showErrorSnackBar("You hit the max amount of items available for this product!", true)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun showAlertDialogToDeleteCartItem(id: String, title: String) {
        val builder = AlertDialog.Builder(context)
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.alert_dialog_title, null)
        builder.setCustomTitle(view)
        builder.setMessage("Are you sure you want to delete $title from your cart?")
        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            dialogInterface.dismiss()
            fireStoreClass.deleteCartItem(context, id, title)
        }
        builder.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss()
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