package com.macode.stopnshop.view.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.telephony.PhoneNumberFormattingTextWatcher
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.ImageSelectionDialogBinding
import com.macode.stopnshop.firebase.FireStoreClass
import com.macode.stopnshop.model.Address
import com.macode.stopnshop.model.CartItem
import com.macode.stopnshop.model.Product
import com.macode.stopnshop.model.User
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

open class BaseActivity : AppCompatActivity() {

    companion object {
        const val CAMERA = 1
        const val GALLERY = 2
        const val PLACE_AUTOCOMPLETE_REQUEST_CODE = 3
        const val ADD_EDIT_ADDRESS_REQUEST_CODE = 4
        const val IMAGE_DIRECTORY = "StopNShopImages"
    }

    var userDetails: User = User()
    var productDetails: Product = Product()
    var addressDetails: Address = Address()
    var productID: String = ""
    lateinit var productList: ArrayList<Product>
    lateinit var cartItemsList: ArrayList<CartItem>
    var userHashMap: HashMap<String, Any> = HashMap<String, Any>()
    var productHashMap: HashMap<String, Any> = HashMap<String, Any>()
    var selectedImageUri: Uri? = null
    var profileImageURL: String? = ""
    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
    val firebaseUserID = firebaseUser?.uid
    val fireStoreClass: FireStoreClass = FireStoreClass()
    val tokenRef = FirebaseMessaging.getInstance().token
    private val storageRef = FirebaseStorage.getInstance()
    val profileImageRef = storageRef.reference.child("ProfileImage${System.currentTimeMillis()}.png")
    val productImageRef = storageRef.reference.child("ProductImage${System.currentTimeMillis()}.png")
    lateinit var fcmToken: String
    var phoneNumberFormattingTextWatcher: PhoneNumberFormattingTextWatcher = PhoneNumberFormattingTextWatcher()
    private var progressDialog: Dialog? = null
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    fun imageSelectionDialog(activity: Activity) {
        val dialog = Dialog(activity)
        val dialogBinding: ImageSelectionDialogBinding = ImageSelectionDialogBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.cameraSelection.setOnClickListener {
            Dexter.withContext(activity).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ).withListener(object: MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if (report.areAllPermissionsGranted()) {
                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            startActivityForResult(intent, CAMERA)
                            dialog.dismiss()
                        } else {
                            showRationalDialogForPermissions()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationalDialogForPermissions()
                }

            }).onSameThread().check()
        }

        dialogBinding.gallerySelection.setOnClickListener {
            Dexter.withContext(activity).withPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE
            ).withListener(object: PermissionListener {
                override fun onPermissionGranted(permission: PermissionGrantedResponse?) {
                    val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(galleryIntent, GALLERY)
                    dialog.dismiss()
                }

                override fun onPermissionDenied(permission: PermissionDeniedResponse?) {
                    showErrorSnackBar("You have denied the storage permission to select image from gallery!", true)
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    showRationalDialogForPermissions()
                }

            }).onSameThread().check()
        }

        dialog.show()
    }

    fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this).setMessage("It looks like you have turned off permission required for this feature. " +
                "It can be enabled under Application Settings.")
            .setPositiveButton("Go to settings")
            {_, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }.setNegativeButton("Cancel")
            {dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    fun convertToImageFile(bitmap: Bitmap): Uri {
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.png")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return Uri.fromFile(file.absoluteFile)
    }

    fun loadUserImage(activity: Activity, image: Any, imageView: ImageView) {
        try {
            Glide
                .with(activity)
                .load(image)
                .centerCrop()
                .placeholder(R.drawable.profile_place_holder)
                .into(imageView)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun getDate(): String {
        val sdf = SimpleDateFormat("MM/dd/yyyy hh:mm:ss", Locale.US)
        return sdf.format(Date())
    }

    fun showProgressDialog(text: String) {
        progressDialog = Dialog(this)
        progressDialog!!.setContentView(R.layout.custom_dialog_progress)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val progressText = progressDialog!!.requireViewById<TextView>(R.id.pleaseWaitText)
            progressText.text = text
        }
        progressDialog!!.show()
    }

    fun hideProgressDialog() {
        progressDialog?.dismiss()
    }

    fun showErrorSnackBar(message: String, errorMessage: Boolean) {
        val snackBar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view

        if (errorMessage) {
            snackBarView.setBackgroundColor(ContextCompat.getColor(this@BaseActivity, R.color.red))
        } else {
            snackBarView.setBackgroundColor(ContextCompat.getColor(this@BaseActivity, R.color.green))
        }
        snackBar.show()
    }

    fun showError(layout: TextInputLayout, message: String) {
        layout.error = message
        layout.requestFocus()
    }

    fun hideError(layout: TextInputLayout) {
        layout.error = null
    }

    fun doubleBackToExit() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true

        showErrorSnackBar("Please click back again to exit application", false)

        Handler(Looper.getMainLooper()).postDelayed({
            doubleBackToExitPressedOnce = false
        }, 2000)
    }
}