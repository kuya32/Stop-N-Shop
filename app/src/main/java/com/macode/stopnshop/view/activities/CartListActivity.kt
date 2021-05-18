package com.macode.stopnshop.view.activities

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.ActivityCartListBinding
import com.macode.stopnshop.model.CartItem
import com.macode.stopnshop.model.Product
import com.macode.stopnshop.utilities.SNSEditText
import com.macode.stopnshop.utilities.SNSTextView
import com.macode.stopnshop.view.adapters.CartListAdapter

class CartListActivity : BaseActivity() {

    private var binding: ActivityCartListBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartListBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        setUpToolbar()
    }

    private fun setUpToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.cartListToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_white)
        supportActionBar?.title = "My Cart"
        toolbar.setTitleTextColor(Color.WHITE)
        supportActionBar?.setBackgroundDrawable(ContextCompat.getDrawable(this@CartListActivity, R.drawable.gradient_background))
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"))
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        getAllProductsList()
    }

    private fun getCartListFromFireStore() {
        fireStoreClass.getCartList(this@CartListActivity)
    }

    fun cartListRetrievalSuccess(cartList: ArrayList<CartItem>) {
        hideProgressDialog()

        for (product in productList) {
            for (cartItem in cartList) {
                if (product.id == cartItem.productID) {
                    cartItem.stockQuantity = product.stockQuantity

                    if (product.stockQuantity.toInt() == 0) {
                        cartItem.cartQuantity = product.stockQuantity
                    }
                }
            }
        }

        cartItemsList = cartList

        if (cartItemsList.size > 0) {
            binding!!.noCartItemsFound.visibility = View.GONE
            binding!!.cartListRecyclerView.visibility = View.VISIBLE

            binding!!.cartListRecyclerView.layoutManager = LinearLayoutManager(this@CartListActivity)
            binding!!.cartListRecyclerView.setHasFixedSize(true)
            val cartAdapter = CartListAdapter(this@CartListActivity, cartList)
            binding!!.cartListRecyclerView.adapter = cartAdapter

            getCartPriceCalculations()

        } else {
            binding!!.noCartItemsFound.visibility = View.VISIBLE
            binding!!.cartListRecyclerView.visibility = View.GONE

            getCartPriceCalculations()
        }
    }

    fun allProductsListSuccess(productsList: ArrayList<Product>) {
        hideProgressDialog()
        productList = productsList

        getCartListFromFireStore()
    }

    private fun getAllProductsList() {
        showProgressDialog("Retrieving your cart...")
        fireStoreClass.getAllProductsList(this@CartListActivity)
    }

    private fun getCartPriceCalculations() {
        var subtotal: Double = String.format("%.2f", 0.00).toDouble()
        for (item in cartItemsList) {
            val availableQuantity = item.stockQuantity.toInt()
            if (availableQuantity > 0) {
                val price = item.price.toDouble()
                val quantity = item.cartQuantity.toInt()
                subtotal += (price * quantity)
            }
        }
        checkCentsDecimalPlacement(subtotal, binding!!.subtotalNumber)

        val taxTotalNumber: Double = subtotal * (.104)
        val taxTotal: Double = String.format("%.2f", taxTotalNumber).toDouble()
        checkCentsDecimalPlacement(taxTotal, binding!!.waSalesTaxNumber)

        // TODO: Figure out a way to calculate shipping price depending on the user's address
        val shippingTotal: Double = String.format("%.2f", 0.00).toDouble()
        checkCentsDecimalPlacement(shippingTotal, binding!!.shippingNumber)

        val totalAmountNumber: Double = subtotal + taxTotal + shippingTotal
        val totalAmount: Double = String.format("%.2f", totalAmountNumber).toDouble()
        checkCentsDecimalPlacement(totalAmount, binding!!.totalAmountNumber)
    }

    private fun checkCentsDecimalPlacement(price: Double, textView: SNSTextView) {
        var numberString = ""
        when (price) {
            0.0 -> {
                numberString = "FREE"
            }
            else -> {
                val cents = price.toString().substringAfter(".")
                numberString = when (cents.length) {
                    1 -> {
                        "${price}0"
                    }
                    else -> {
                        price.toString()
                    }
                }
            }
        }
        "$$numberString".also { textView.text = it }
    }

    fun deleteCartItemSuccess(title: String) {
        hideProgressDialog()
        showErrorSnackBar("$title successfully deleted from your cart!", false)
        getCartListFromFireStore()
    }

    fun cartItemUpdateSuccess() {
        getCartListFromFireStore()
    }
}