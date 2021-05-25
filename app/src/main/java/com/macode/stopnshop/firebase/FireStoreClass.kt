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
import com.macode.stopnshop.model.*
import com.macode.stopnshop.utilities.Constants
import com.macode.stopnshop.view.activities.*
import com.macode.stopnshop.view.fragments.*

class FireStoreClass {
    private val fireStore = FirebaseFirestore.getInstance()
    private val userReference = fireStore.collection("Users")
    private val productReference = fireStore.collection("Products")
    private val cartItemReference = fireStore.collection("CartItems")
    private val addressReference = fireStore.collection("Addresses")
    private val paymentReference = fireStore.collection("Payments")
    private val orderReference = fireStore.collection("Orders")
    private val soldProductReference = fireStore.collection("SoldProducts")

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

    fun getCartList(activity: Activity) {
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
                when (activity) {
                    is CartListActivity -> {
                        activity.cartListRetrievalSuccess(cartList)
                    }
                    is CheckoutActivity -> {
                        activity.cartListRetrievalSuccess(cartList)
                    }
                }

            }.addOnFailureListener { e ->
                when (activity) {
                    is CartListActivity -> {
                        activity.hideProgressDialog()
                    }
                    is CheckoutActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error retrieving the cart list", e)
                showErrorSnackBar(activity, "Sorry, we couldn't retrieve your cart list!", true)
            }
    }

    fun getAllProductsList(activity: Activity) {
        productReference.get().addOnSuccessListener { document ->
            Log.i(activity.javaClass.simpleName, document.documents.toString())
            val productsList: ArrayList<Product> = ArrayList()
            for (i in document.documents) {
                val product = i.toObject(Product::class.java)
                product!!.id = i.id

                productsList.add(product)
            }
            when (activity) {
                is CartListActivity -> {
                    activity.allProductsListSuccess(productsList)
                }
                is CheckoutActivity -> {
                    activity.allProductListSuccess(productsList)
                }
            }


        }.addOnFailureListener { e ->
            when (activity) {
                is CartListActivity -> {
                    activity.hideProgressDialog()
                }
                is CheckoutActivity -> {
                    activity.hideProgressDialog()
                }
            }
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

    fun checkAddressListItemsForDefault(
        activity: AddEditAddressActivity,
        fullName: String,
        phone: String,
        address: String,
        city: String,
        state: String,
        zipcode: String,
        lat: String,
        long: String,
        info: String,
        type: String,
        otherDetails: String,
        default: Boolean
    ) {
        addressReference
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .whereEqualTo("default", "true")
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                val defaultList: ArrayList<Address> = ArrayList()
                for (i in document.documents) {
                    val addressItem = i.toObject(Address::class.java)
                    addressItem!!.id = i.id
                    defaultList.add(addressItem)
                }
                if (defaultList.size == 0) {
                    activity.defaultChangeSuccess(fullName, phone, address, city, state, zipcode, lat, long, info, type, otherDetails, default)
                } else {
                    addressReference.document(defaultList[0].id).update("default", "false").addOnSuccessListener {
                    activity.defaultChangeSuccess(fullName, phone, address, city, state, zipcode, lat, long, info, type, otherDetails, default)
                }.addOnFailureListener { e ->
                    Log.e(activity.javaClass.simpleName, "Error changing the default address item", e)
                    activity.showErrorSnackBar("Sorry, we couldn't change your default address item!", true)
                }
                }
            }.addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName, "Error loading the default list", e)
                activity.showErrorSnackBar("Sorry, we couldn't load your default list!", true)
            }
    }

    fun deleteAddress(activity: AddressListActivity, addressID: String, addressCity: String) {
        addressReference.document(addressID).delete().addOnSuccessListener {
            activity.addressDeleteSuccess(addressCity)
        }.addOnFailureListener { e ->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName, "Error deleting address", e)
            activity.showErrorSnackBar("Sorry, we couldn't delete your address!", true)
        }
    }

    fun addPaymentMethod(activity: AddEditPaymentActivity, paymentInfo: Payment) {
        paymentReference.document().set(paymentInfo, SetOptions.merge()).addOnSuccessListener {
            activity.addEditPaymentSuccess()
        }.addOnFailureListener { e ->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName, "Error while adding the payment method", e)
            activity.showErrorSnackBar("Sorry, we couldn't add your payment method!", true)
        }
    }

    fun getPaymentList(activity:PaymentListActivity) {
        paymentReference.whereEqualTo(Constants.USER_ID, getCurrentUserID()).get().addOnSuccessListener { document ->
            Log.i(activity.javaClass.simpleName, document.documents.toString())
            val paymentList: ArrayList<Payment> = ArrayList()
            for (i in document.documents) {
                val payment = i.toObject(Payment::class.java)
                payment!!.id = i.id
                paymentList.add(payment)
            }
            activity.paymentListRetrievalSuccess(paymentList)
        }.addOnFailureListener { e ->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName, "Error loading the payment method list", e)
            activity.showErrorSnackBar("Sorry, we couldn't load your payment method list!", true)
        }
    }

    fun updatePaymentMethod(activity: AddEditPaymentActivity, paymentInfo: Payment, paymentID: String) {
        paymentReference.document(paymentID).set(paymentInfo, SetOptions.merge()).addOnSuccessListener {
            activity.addEditPaymentSuccess()
        }.addOnFailureListener { e ->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName, "Error updating payment method", e)
            activity.showErrorSnackBar("Sorry, we couldn't update your payment method!", true)
        }
    }

    fun checkPaymentListItemsForDefault(
        activity: AddEditPaymentActivity,
        cardName: String,
        cardNumber: String,
        month: String,
        year: String,
        verificationValue: String,
        default: Boolean
    ) {
        paymentReference
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .whereEqualTo("default", "true")
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                val defaultList: ArrayList<Payment> = ArrayList()
                for (i in document.documents) {
                    val payment = i.toObject(Payment::class.java)
                    payment!!.id = i.id
                    defaultList.add(payment)
                }
                if (defaultList.size == 0) {
                    activity.defaultChangeSuccess(cardName, cardNumber, month, year, verificationValue, default)
                } else {
                    paymentReference.document(defaultList[0].id).update("default", "false").addOnSuccessListener {
                        activity.defaultChangeSuccess(cardName, cardNumber, month, year, verificationValue, default)
                    }.addOnFailureListener { e ->
                        Log.e(activity.javaClass.simpleName, "Error changing the default payment method item", e)
                        activity.showErrorSnackBar("Sorry we couldn't change your default payment method item!", true)
                    }
                }
            }.addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName, "Error loading the default list", e)
                activity.showErrorSnackBar("Sorry, we couldn't load your default list!", true)
            }
    }

    fun deletePaymentMethod(activity: PaymentListActivity, paymentID: String, paymentNumber: String) {
        paymentReference.document(paymentID).delete().addOnSuccessListener {
            val cardNumberEnding = paymentNumber.substring(paymentNumber.length - 4)
            activity.paymentMethodSuccessfullyDeleted(cardNumberEnding)
        }.addOnFailureListener { e ->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName, "Error deleting payment method", e)
            activity.showErrorSnackBar("Sorry, we couldn't delete your payment method!", true)
        }
    }

    fun retrieveDefaultAddress(activity: CheckoutActivity) {
        val defaultAddressList: ArrayList<Address> = ArrayList()
        addressReference
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .whereEqualTo("default", "true")
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                for (i in document.documents) {
                    val addressItem = i.toObject(Address::class.java)
                    addressItem!!.id = i.id
                    defaultAddressList.add(addressItem)
                }
                if (defaultAddressList.size == 0) {
                    activity.chooseAddressForCheckout()
                } else {
                    activity.establishAddressForCheckout(defaultAddressList[0])
                }
            }.addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName, "Error loading the default address list", e)
                activity.showErrorSnackBar(
                    "Sorry, we couldn't load your default address list!",
                    true
                )
            }
    }

    fun retrieveDefaultPayment(activity: CheckoutActivity) {
        val defaultPaymentList: ArrayList<Payment> = ArrayList()
        paymentReference
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .whereEqualTo("default", "true")
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                for (i in document.documents) {
                    val paymentItem = i.toObject(Payment::class.java)
                    paymentItem!!.id = i.id
                    defaultPaymentList.add(paymentItem)
                }
                if (defaultPaymentList.size == 0) {
                    activity.choosePaymentForCheckout()
                } else {
                    activity.establishPaymentForCheckout(defaultPaymentList[0])
                }
            }.addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName, "Error loading the default payment list", e)
                activity.showErrorSnackBar(
                    "Sorry, we couldn't load your default payment list!",
                    true
                )
            }
    }

    fun setDefaultAddress(activity: AddressListActivity, address: Address) {
        addressReference.document(address.id).update("default", "true").addOnSuccessListener {
            activity.defaultAddressSuccess(address, true)
        }.addOnFailureListener { e ->
            Log.e(activity.javaClass.simpleName, "Error setting default address", e)
            Toast.makeText(activity, "Sorry, we couldn't set address as default!", Toast.LENGTH_SHORT).show()
        }
    }

    fun setDefaultPayment(activity: PaymentListActivity, payment: Payment) {
        paymentReference.document(payment.id).update("default", "true").addOnSuccessListener {
            activity.defaultPaymentSuccess(payment, true)
        }.addOnFailureListener { e ->
            Log.e(activity.javaClass.simpleName, "Error setting default payment method", e)
            Toast.makeText(activity, "Sorry, we couldn't set payment method as default!", Toast.LENGTH_SHORT).show()
        }
    }

    fun placeOrder(activity: CheckoutActivity, order: Order) {
        orderReference.document().set(order, SetOptions.merge()).addOnSuccessListener {
            activity.orderPlacedSuccess()
        }.addOnFailureListener { e ->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName, "Error while placing order", e)
            activity.showErrorSnackBar("Sorry, we couldn't place your order!", true)
        }
    }

    fun updateProductAndCartDetails(activity: CheckoutActivity, cartList: ArrayList<CartItem>, order: Order) {
        val writeBatch = fireStore.batch()

        for (cartItem in cartList) {
            val productHashmap = HashMap<String, Any>()
            productHashmap["stockQuantity"] = (cartItem.stockQuantity.toInt() - cartItem.cartQuantity.toInt()).toString()

            val productDocumentReference = productReference.document(cartItem.productID)
            writeBatch.update(productDocumentReference, productHashmap)

            val soldProduct = SoldProduct(
                cartItem.productOwnerID,
                getCurrentUserID(),
                cartItem.title,
                cartItem.price,
                cartItem.cartQuantity,
                cartItem.image,
                order.title,
                order.dateOrderPlaced,
                order.subTotalAmount,
                order.waTaxAmount,
                order.shippingAmount,
                order.totalAmount,
                order.address,
                order.payment
            )
            val soldProductDocumentReference = soldProductReference.document(cartItem.productID)
            writeBatch.set(soldProductDocumentReference, soldProduct)
        }

        for (cartItem in cartList) {
            val cartItemDocumentReference = cartItemReference.document(cartItem.id)
            writeBatch.delete(cartItemDocumentReference)
        }

        writeBatch.commit().addOnSuccessListener {
            activity.productAndCartDetailsUpdatedSuccessfully()
        }.addOnFailureListener { e ->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName, "Error updating product stock and deleting cart", e)
            activity.showErrorSnackBar("We couldn't update the product stock and delete your cart items!", true)
        }
    }

    fun getMyOrdersList(fragment: OrdersFragment) {
        orderReference.whereEqualTo(Constants.USER_ID, getCurrentUserID()).get().addOnSuccessListener { document ->
            Log.i(fragment.javaClass.simpleName, document.documents.toString())
            val orderList: ArrayList<Order> = ArrayList()
            for (i in document.documents) {
                val orderItem = i.toObject(Order::class.java)!!
                orderItem.id = i.id
                orderList.add(orderItem)
            }
            fragment.populateOrdersListInUI(orderList)
        }.addOnFailureListener { e ->
            fragment.hideProgressDialog()
            Log.e(fragment.javaClass.simpleName, "Error retrieving user order list", e)
            fragment.showErrorSnackBar("Sorry, we couldn't retrieve your order list!", true)
        }
    }

    fun deleteOrder(fragment: OrdersFragment, orderID: String) {
        orderReference.document(orderID).delete().addOnSuccessListener {
            fragment.deleteOrderSuccess()
        }.addOnFailureListener { e ->
            fragment.hideProgressDialog()
            Log.e(fragment.javaClass.simpleName, "Error deleting orderItem from Firestore", e)
            showErrorSnackBar(fragment.requireActivity(), "Sorry, we couldn't delete the order item from the database!", true)
        }
    }

    fun getSoldProductsList(fragment: SoldProductsFragment) {
        soldProductReference.whereEqualTo("ownerID", getCurrentUserID()).get().addOnSuccessListener { document ->
            Log.i(fragment.javaClass.simpleName, document.documents.toString())
            val soldProductList: ArrayList<SoldProduct> = ArrayList()
            for (i in document.documents) {
                val soldProduct = i.toObject(SoldProduct::class.java)!!
                soldProduct.id = i.id
                soldProductList.add(soldProduct)
            }
            fragment.soldProductListRetrievalSuccess(soldProductList)
        }.addOnFailureListener { e ->
            fragment.hideProgressDialog()
            Log.e(fragment.javaClass.simpleName, "Error getting sold products list", e)
            fragment.showErrorSnackBar("Sorry, we couldn't get your sold product list", true)
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