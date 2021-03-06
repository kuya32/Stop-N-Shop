package com.macode.stopnshop.view.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.ActivityAddEditProductBinding
import com.macode.stopnshop.model.Product
import com.macode.stopnshop.utilities.Constants
import com.macode.stopnshop.utilities.SNSTextView

class AddEditProductActivity : BaseActivity() {

    private var binding: ActivityAddEditProductBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditProductBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        if (intent.hasExtra(Constants.PRODUCT_DETAILS)) {
            productDetails = intent.getParcelableExtra(Constants.PRODUCT_DETAILS)!!
            establishProductInfo(productDetails)
        }

        setUpToolbar()

        binding!!.addProductImageButton.setOnClickListener {
            imageSelectionDialog(this@AddEditProductActivity)
        }

        binding!!.addProductSubmitButton.setOnClickListener {
            validateProductInformation()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA -> {
                    data?.extras?.let {
                        val bitmap: Bitmap = data.extras!!.get("data") as Bitmap
                        selectedImageUri = convertToImageFile(bitmap)
                        loadUserImage(this@AddEditProductActivity, selectedImageUri!!, binding!!.addProductImage)
                    }
                }
                GALLERY -> {
                    data?.let {
                        Glide
                            .with(this@AddEditProductActivity)
                            .load(data.data)
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .listener(object: RequestListener<Drawable> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    Log.e("GallerySelection", "Error loading image!", e)
                                    showErrorSnackBar("Error loading the image from gallery!", true)
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    resource?.let {
                                        val bitmap: Bitmap = resource.toBitmap()
                                        selectedImageUri = convertToImageFile(bitmap)
                                        Log.i("ImagePath", selectedImageUri.toString())
                                    }
                                    return false
                                }
                            })
                            .placeholder(R.drawable.profile_place_holder)
                            .into(binding!!.addProductImage)
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED){
            Log.e("SetUpActionCancelled", "User cancelled action!")
            showErrorSnackBar("Action cancelled!", true)
        }
    }

    private fun setUpToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.addProductToolbar)
        val toolbarTitle = toolbar.findViewById<SNSTextView>(R.id.toolbarTitle)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_white)
        if (intent.hasExtra(Constants.PRODUCT_DETAILS)) {
            toolbarTitle.text = resources.getString(R.string.editProduct)
        } else {
            toolbarTitle.text = resources.getString(R.string.addProduct)
        }
        toolbarTitle.setTextColor(resources.getColor(R.color.white, theme))
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"))
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun establishProductInfo(productDetails: Product) {
        loadUserImage(this@AddEditProductActivity, productDetails.image, binding!!.addProductImage)
        binding!!.productTitleEditInput.setText(productDetails.title)
        binding!!.productPriceEditInput.setText(productDetails.price)
        binding!!.productDescriptionEditInput.setText(productDetails.description)
        binding!!.productQuantityEditInput.setText(productDetails.stockQuantity)
    }

    private fun validateProductInformation() {
        val title = binding!!.productTitleEditInput.text.toString()
        val price = binding!!.productPriceEditInput.text.toString()
        val description = binding!!.productDescriptionEditInput.text.toString()
        val quantity = binding!!.productQuantityEditInput.text.toString()

        when {
            title.isEmpty() -> {
                showError(binding!!.productTitleInput, "Please enter a title for the product!")
            }
            price.isEmpty() || price == "0" || price == "0.00" -> {
                hideError(binding!!.productTitleInput)
                showError(binding!!.productPriceInput, "Please enter the price of the product!")
            }
            description.isEmpty() -> {
                hideError(binding!!.productPriceInput)
                showError(binding!!.productDescriptionInput, "Please enter a description for the product!")
            }
            quantity.isEmpty() || quantity == "0" -> {
                hideError(binding!!.productDescriptionInput)
                showError(binding!!.productQuantityInput, "Please enter the quantity available!")
            }
            else -> {
                showProgressDialog("Adding product...")
                savingProductInfoToFirebase(selectedImageUri!!, title, price, description, quantity)
            }
        }
    }

    private fun savingProductInfoToFirebase(image: Uri, title: String, price: String, description: String, quantity: String) {
        productImageRef.putFile(image).addOnSuccessListener { taskSnapshot ->
            Log.i("ProductImageURL", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                Log.i("DownloadableImageURL", uri.toString())
                profileImageURL = uri.toString()
                val usersName = this.getSharedPreferences(Constants.STOP_N_SHOP_PREFERENCE, Context.MODE_PRIVATE).getString(Constants.LOGGED_IN_USERS_NAME, "")
                val product = Product(
                    fireStoreClass.getCurrentUserID(),
                    usersName!!,
                    title,
                    price,
                    description,
                    quantity,
                    profileImageURL.toString()
                )
                fireStoreClass.uploadProductDetails(this@AddEditProductActivity, product)
            }
        }.addOnFailureListener { e ->
            showErrorSnackBar("${e.message}", true)
            hideProgressDialog()
        }
    }

    fun productUploadSuccessful() {
        hideProgressDialog()
        finish()
    }

}