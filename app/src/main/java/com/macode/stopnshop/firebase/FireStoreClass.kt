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
import com.macode.stopnshop.model.Address
import com.macode.stopnshop.model.CartItem
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
    private val cartItemReference = fireStore.collection("CartItems")
    private val addressReference = fireStore.collection("Addresses")

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

    fun establishSeller(activity: ProductDetailActivity, sellersID: String) {
        userReference.document(sellersID).get().addOnSuccessListener { document ->
            val sellersInfo = document.toObject(User::class.java)
            activity.receiveSellerInfoSuccess(sellersInfo)
        }.addOnFailureListener { e ->
            activity.hideProgressDialog()
            Log.e("ProductSellersInfo", "Error retrieving product sellers info", e)
            showErrorSnackBar(activity, "Sorry, couldn't get sellers info!", true)
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
                is AddEditProductActivity -> {
                    activity.productUploadSuccessful()
                }
            }
        }.addOnFailureListener { e ->
            when (activity) {
                is AddEditProductActivity -> {
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

    fun deleteProduct(fragment: ProductsFragment, productID: String) {
        productReference.document(productID).delete().addOnSuccessListener {
            fragment.deleteProductSuccess()
        }.addOnFailureListener { e ->
            fragment.hideProgressDialog()
            Log.e(fragment.javaClass.simpleName, "Error deleting product from Firestore", e)
            showErrorSnackBar(fragment.requireActivity(), "Sorry, we couldn't delete the product from the database!", true)
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

    fun addCartItems(activity: ProductDetailActivity, addToCart: CartItem) {
        cartItemReference.document().set(addToCart, SetOptions.merge()).addOnSuccessListener {
            activity.addToCartSuccess(addToCart.cartQuantity, addToCart.title)
        }.addOnFailureListener { e ->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName, "Error while adding product item to cart", e)
            showErrorSnackBar(activity, "Sorry, we couldn't add product item to cart!", true)
        }
    }

    fun checkIfItemExistsInCart(activity: DashboardActivity) {
        cartItemReference
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                if (document.size() > 0) {
                    activity.itemsFoundInCart()
                } else {
                    activity.hideCartFabButton()
                }
            }.addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName, "Error while checking the existing cart list", e)
                showErrorSnackBar(activity, "Sorry, we ran into an error checking your cart!", true)
            }
    }

    fun getCartList(activity: CartListActivity) {
        cartItemReference
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i("CartList", document.documents.toString())
                val cartList: ArrayList<CartItem> = ArrayList()
                for (i in document.documents) {
                    val cartItem = i.toObject(CartItem::class.java)
                    cartItem!!.id = i.id

                    cartList.add(cartItem)
                }
                activity.cartListRetrievalSuccess(cartList)
            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error retrieving the cart list", e)
                showErrorSnackBar(activity, "Sorry, we couldn't retrieve your cart list!", true)
            }
    }

    fun getAllProductsList(activity: CartListActivity) {
        productReference.get().addOnSuccessListener { document ->
            Log.i(activity.javaClass.simpleName, document.documents.toString())
            val productsList: ArrayList<Product> = ArrayList()
            for (i in document.documents) {
                val product = i.toObject(Product::class.java)
                product!!.id = i.id

                productsList.add(product)
            }

            activity.allProductsListSuccess(productsList)
        }.addOnFailureListener { e ->
            activity.hideProgressDialog()
            Log.e("AllProductsList", "Error while getting all products list!", e)
        }
    }

    fun deleteCartItem(context: Context, id: String, title: String) {
        cartItemReference.document(id).delete().addOnSuccessListener {
            when (context) {
                is CartListActivity -> {
                    context.deleteCartItemSuccess(title)
                }
            }
        }.addOnFailureListener { e ->
            when (context) {
                is CartListActivity -> {
                    context.hideProgressDialog()
                    Log.e(context.javaClass.simpleName, "Error deleting cart item from Firestore", e)
                    showErrorSnackBar(context, "Sorry, we couldn't delete the item from your cart!", true)
                }
            }

        }
    }

    fun updateMyCart(context: Context, id: String, cartItemHashMap: HashMap<String, Any>) {
        cartItemReference.document(id).update(cartItemHashMap).addOnSuccessListener {
            when (context) {
                is CartListActivity -> {
                    context.cartItemUpdateSuccess()
                }
            }
        }.addOnFailureListener { e ->
            when (context) {
                is CartListActivity -> {
                    context.showErrorSnackBar("Sorry, we couldn't update the cart item!", true)
                }
            }
            Log.e(context.javaClass.simpleName, "Error while updating the cart item.", e)
        }
    }

    fun addAddress(activity: AddEditAddressActivity, addressInfo: Address) {
        addressReference.document().set(addressInfo, SetOptions.merge()).addOnSuccessListener {
            activity.addEditAddressSuccess(addressInfo.city)
        }.addOnFailureListener { e ->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName, "Error while adding the address", e)
            activity.showErrorSnackBar("Sorry, we couldn't add your address!", true)
        }
    }

    fun getAddressList(activity: AddressListActivity) {
        addressReference.whereEqualTo(Constants.USER_ID, getCurrentUserID()).get().addOnSuccessListener { document ->
            Log.i(activity.javaClass.simpleName, document.documents.toString())
            val addressList: ArrayList<Address> = ArrayList()
            for (i in document.documents) {
                val address = i.toObject(Address::class.java)
                address!!.id = i.id
                addressList.add(address)
            }
            activity.addressListRetrievalSuccess(addressList)
        }.addOnFailureListener { e ->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName, "Error loading the address list", e)
            activity.showErrorSnackBar("Sorry, we couldn't load your address list!", true)
        }
    }

    fun updateAddress(activity: AddEditAddressActivity, addressInfo: Address, addressID: String) {
        addressReference.document(addressID).set(addressInfo, SetOptions.merge()).addOnSuccessListener {
            activity.updateAddressSuccess()
        }.addOnFailureListener { e ->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName, "Error updating address", e)
            activity.showErrorSnackBar("Sorry, we couldn't update your address!", true)
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