package com.macode.stopnshop.firebase

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.macode.stopnshop.R
import com.macode.stopnshop.model.Product
import com.macode.stopnshop.model.User
import com.macode.stopnshop.utilities.Constants
import com.macode.stopnshop.view.activities.*
import com.macode.stopnshop.view.fragments.DashboardFragment
import com.macode.stopnshop.view.fragments.EditEmailFragment
import com.macode.stopnshop.view.fragments.ProductsFragment

class FireStoreClass {
    private val fireStore = FirebaseFirestore.getInstance()
    private val userReference = fireStore.collection("Users")
    private val productReference = fireStore.collection("Products")

    fun registerUser(activity: RegisterActivity, userInfo: User) {
        userReference.document(getCurrentUserID()).set(userInfo, SetOptions.merge()).addOnSuccessListener {
            activity.hideProgressDialog()
            logoutUser(activity)
        }.addOnFailureListener { e ->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName, "User registration failed", e)
            showErrorSnackBar(activity, "Error registering user. Please try again!", true)
        }
    }

    fun establishUser(activity: Activity) {
        userReference.document(getCurrentUserID()).get().addOnSuccessListener { document ->
            val loggedUser = document.toObject(User::class.java)!!
            when (activity) {
                is LoginActivity -> {
                    val sharedPreferences = activity.getSharedPreferences(Constants.STOP_N_SHOP_PREFERENCE, Context.MODE_PRIVATE)
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putString(Constants.LOGGED_IN_USERS_NAME, "${loggedUser.firstName} ${loggedUser.lastName}")
                    editor.apply()
                    userReference.document(getCurrentUserID()).update("status", "Online").addOnSuccessListener {
                        activity.loginSuccess(loggedUser)
                    }.addOnFailureListener { e ->
                        activity.hideProgressDialog()
                        Log.e(activity.javaClass.simpleName, "Error logging in user", e)
                        showErrorSnackBar(activity, "Error logging in user. Please try again!", true)
                    }
                }
                is SettingsActivity -> {
                    activity.userDetailsSuccess(loggedUser)
                }
            }
        }.addOnFailureListener { e ->
            when (activity) {
                is LoginActivity -> {
                    activity.hideProgressDialog()
                }
                is SettingsActivity -> {
                    activity.hideProgressDialog()
                }
            }
            Log.e("LogInUser", "Error writing document", e)
        }
    }

    fun updateUserAccountInfo(activity: Activity, userHashMap: HashMap<String, Any>) {
        userReference.document(getCurrentUserID()).update(userHashMap).addOnSuccessListener {
            Log.i(activity.javaClass.simpleName, "User info updated successfully")
            showErrorSnackBar(activity, "Account info updated!", false)
            when (activity) {
                is SetUpActivity -> {
                    activity.updateUserSuccess()
                }
                is EditUserProfileActivity -> {
                    activity.updateUserSuccess()
                }
            }
        }.addOnFailureListener { e ->
            when (activity) {
                is SetUpActivity -> {
                    activity.hideProgressDialog()
                }
                is EditUserProfileActivity -> {
                    activity.hideProgressDialog()
                }
            }
            Log.e(activity.javaClass.simpleName, "Error while updating user info", e)
            showErrorSnackBar(activity, "Error while updating user info!", true)
        }
    }

    fun updateUserEmail(fragment: EditEmailFragment, newEmail: String) {
        userReference.document(getCurrentUserID()).update("email", newEmail).addOnSuccessListener {
            fragment.updateEmailSuccess()
        }.addOnFailureListener { e ->
            fragment.hideProgressDialog()
            Log.e(fragment.javaClass.simpleName, "Failed to update email in FireStore", e)
            fragment.showErrorSnackBar("Sorry, we couldn't update your email to the database!", true)
        }
    }

    fun uploadProductDetails(activity: Activity, productInfo: Product) {
        productReference.document().set(productInfo, SetOptions.merge()).addOnSuccessListener {
            when (activity) {
                is AddProductActivity -> {
                    activity.productUploadSuccessful()
                }
            }
        }.addOnFailureListener { e ->
            when (activity) {
                is AddProductActivity -> {
                    activity.hideProgressDialog()
                }
            }
            Log.e(activity.javaClass.simpleName, "Error while uploading the product details.", e)
            showErrorSnackBar(activity, "Error while uploading the product details", true)
        }
    }

    fun getProductsList(fragment: Fragment) {
        productReference.whereEqualTo(Constants.USER_ID, getCurrentUserID()).get().addOnSuccessListener { document ->
            Log.i("ProductList", document.documents.toString())
            val productsList: ArrayList<Product> = ArrayList()
            for (i in document.documents) {
                val product = i.toObject(Product::class.java)
                product!!.id = i.id

                productsList.add(product)
            }

            when (fragment) {
                is ProductsFragment -> {
                    fragment.successProductsListFromFireStore(productsList)
                }
            }
        }.addOnFailureListener { e ->
            Log.e(fragment.javaClass.simpleName, "Error accessing products list", e)
            showErrorSnackBar(fragment.requireActivity(), "Sorry, we could not retrieve the products list!", true)
        }
    }

    fun getDashboardList(fragment: DashboardFragment) {
        productReference.get().addOnSuccessListener { document ->
            Log.i("DashboardList", document.documents.toString())
            val productList: ArrayList<Product> = ArrayList()
            for (i in document.documents) {
                val product = i.toObject(Product::class.java)
                product!!.id = i.id

                productList.add(product)
            }
            fragment.successDashboardListFromFireStore(productList)
        }.addOnFailureListener { e ->
            fragment.hideProgressDialog()
            Log.e(fragment.javaClass.simpleName, "Error while getting dashboard items list", e)
        }
    }

    fun logoutUser(activity: Activity) {
        userReference.document(getCurrentUserID()).update("status", "Offline").addOnSuccessListener {
            FirebaseAuth.getInstance().signOut()
            when (activity) {
                is RegisterActivity -> {
                    activity.successfulLogout()
                }
                is SettingsActivity -> {
                    activity.successfulLogout()
                }
            }
        }.addOnFailureListener { e ->
            Log.e(activity.javaClass.simpleName, "Error logging out user", e)
            Toast.makeText(activity, "Sorry, we can't log you out!", Toast.LENGTH_SHORT).show()
        }
    }

    fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    private fun showErrorSnackBar(activity: Activity, message: String, errorMessage: Boolean) {
        val snackBar = Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view

        if (errorMessage) {
            snackBarView.setBackgroundColor(ContextCompat.getColor(activity, R.color.red))
        } else {
            snackBarView.setBackgroundColor(ContextCompat.getColor(activity, R.color.green))
        }
        snackBar.show()
    }
}