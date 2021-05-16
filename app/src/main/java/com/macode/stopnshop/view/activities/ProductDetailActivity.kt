package com.macode.stopnshop.view.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.ActivityProductDetailBinding
import com.macode.stopnshop.model.Product
import com.macode.stopnshop.model.User
import com.macode.stopnshop.utilities.Constants
import com.macode.stopnshop.utilities.SNSTextView

class ProductDetailActivity : BaseActivity() {

    private var binding: ActivityProductDetailBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        if (intent.hasExtra(Constants.PRODUCT_DETAILS)) {
            productDetails = intent.getParcelableExtra(Constants.PRODUCT_DETAILS)!!
        }

        establishProductDetails(productDetails)

        setUpToolbar()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        when (productDetails.userID) {
            firebaseUserID -> {
                menuInflater.inflate(R.menu.product_details_menu, menu)
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.editAction -> {
                val intent = Intent(this@ProductDetailActivity, AddEditProductActivity::class.java)
                intent.putExtra(Constants.PRODUCT_DETAILS, productDetails)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun establishProductDetails(productDetails: Product) {
        showProgressDialog("Loading product details...")
        loadUserImage(this@ProductDetailActivity, productDetails.image, binding!!.productDetailsImage)
        binding!!.productDetailsTitle.text = productDetails.title
        "$${productDetails.price}".also { binding!!.productDetailsPrice.text = it }
        binding!!.productDetailsDescription.text = productDetails.description
        binding!!.productDetailsAvailableQuantity.text = productDetails.stockQuantity
        binding!!.productDetailsSellersName.text = productDetails.usersName
        fireStoreClass.establishSeller(this@ProductDetailActivity, productDetails.userID)
    }

    private fun setUpToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.productDetailsToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_white)
        "${productDetails.title} Details".also { supportActionBar?.title = it }
        toolbar.setTitleTextColor(Color.WHITE)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"))
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun receiveSellerInfoSuccess(sellersInfo: User?) {
        sellersInfo?.image?.let {
            loadUserImage(this@ProductDetailActivity, it, binding!!.productDetailsSellersImage)
        }
        binding!!.productDetailsSellersLocation.text = sellersInfo?.location
        binding!!.productDetailsSellersPhone.text = sellersInfo?.phone
        when (sellersInfo?.status) {
            "Online" -> {
                binding!!.productDetailsSellersStatusIndicator.setColorFilter(Color.GREEN)
            }
            else -> {
                binding!!.productDetailsSellersStatusIndicator.setColorFilter(Color.RED)
            }
        }
        binding!!.productDetailsSellersStatus.text = sellersInfo?.status
        hideProgressDialog()
    }
}