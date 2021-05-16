package com.macode.stopnshop.view.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.ActivityProductDetailBinding
import com.macode.stopnshop.databinding.ProductAmountDeterminationDialogBinding
import com.macode.stopnshop.model.Product
import com.macode.stopnshop.model.User
import com.macode.stopnshop.utilities.Constants

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
            else -> {
                menuInflater.inflate(R.menu.product_details_add_to_cart_menu, menu)
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
            R.id.addToCart -> {
                productDeterminationDialog()
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

    fun addToCartSuccess() {
        hideProgressDialog()
    }

    private fun productDeterminationDialog() {
        val dialog = Dialog(this@ProductDetailActivity)
        val dialogBinding: ProductAmountDeterminationDialogBinding = ProductAmountDeterminationDialogBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialogBinding.productAmountAvailableQuantity.text = productDetails.stockQuantity
        val amountAvailable = dialogBinding.productAmountAvailableQuantity.text.toString().toInt()
        var desiredAmount = dialogBinding.productAmountDesiredQuantity.text.toString().toInt()

        dialogBinding.productAmountMinusButton.setOnClickListener {
            if (desiredAmount <= 1) {
                showErrorSnackBar("Must add a least one item!", true)
            } else {
                desiredAmount--
                dialogBinding.productAmountDesiredQuantity.text = desiredAmount.toString()
            }
        }

        dialogBinding.productAmountPlusButton.setOnClickListener {
            if (desiredAmount == amountAvailable) {
                showErrorSnackBar("You hit the max amount of items available for this product!", true)
            } else {
                desiredAmount++
                dialogBinding.productAmountDesiredQuantity.text = desiredAmount.toString()
            }
        }

//        dialogBinding.addToCartButton.setOnClickListener {
//
//            if (email.isEmpty() || !email.contains("@") || !email.contains(".")) {
//                showError(dialogBinding.sensitiveEmailInput, "Please enter a valid email address!")
//            } else if (password.isEmpty()) {
//                hideError(dialogBinding.sensitiveEmailInput)
//                showError(dialogBinding.sensitivePasswordInput, "Please enter your password!")
//            } else if (!dialogBinding.emailRadioButton.isChecked && !dialogBinding.passwordRadioButton.isChecked) {
//                hideError(dialogBinding.sensitivePasswordInput)
//                showErrorSnackBar("Please select credential type to change!", true)
//            } else {
//                hideError(dialogBinding.sensitivePasswordInput)
//                authCredential = EmailAuthProvider.getCredential(email, password)
//                firebaseUser?.reauthenticate(authCredential)?.addOnSuccessListener {
//                    hideError(dialogBinding.sensitiveEmailInput)
//                    hideError(dialogBinding.sensitivePasswordInput)
//                    if (credentials == "Email") {
//                        dialog.dismiss()
//                        binding!!.settingsMainRelative.visibility = View.GONE
//                        binding!!.settingsSecondaryRelative.visibility = View.VISIBLE
//                        detailBundle.putString("fragmentId", credentials)
//                        editEmailFragment.arguments = detailBundle
//                        supportFragmentManager.beginTransaction().replace(R.id.settingsFragmentContainer, editEmailFragment, Constants.EMAIL_FRAGMENT).commit()
//                    } else if (credentials == "Password") {
//                        dialog.dismiss()
//                        binding!!.settingsMainRelative.visibility = View.GONE
//                        binding!!.settingsSecondaryRelative.visibility = View.VISIBLE
//                        detailBundle.putString("fragmentId", credentials)
//                        editPasswordFragment.arguments = detailBundle
//                        supportFragmentManager.beginTransaction().replace(R.id.settingsFragmentContainer, editPasswordFragment).commit()
//                    }
//                }?.addOnFailureListener {
//                    showError(dialogBinding.sensitiveEmailInput, "Email or password are incorrect!")
//                    showError(dialogBinding.sensitivePasswordInput, "Email or password are incorrect!")
//                }
//            }
//        }

        dialog.show()
        val window: Window? = dialog.window
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }
}