package com.macode.stopnshop.firebase

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.macode.stopnshop.model.User
import com.macode.stopnshop.utilities.Constants
import com.macode.stopnshop.view.activities.LoginActivity
import com.macode.stopnshop.view.activities.RegisterActivity

class FireStoreClass {
    private val fireStore = FirebaseFirestore.getInstance()
    private val userReference = fireStore.collection("Users")

    fun registerUser(activity: RegisterActivity, userInfo: User) {
        userReference.document(getCurrentUserID()).set(userInfo, SetOptions.merge()).addOnSuccessListener {
            activity.hideProgressDialog()
            logoutUser(activity)
        }.addOnFailureListener { e ->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName, "User registration failed", e)
            Toast.makeText(activity, "Error registering user. Please try again!", Toast.LENGTH_SHORT).show()
        }
    }

    fun establishUser(activity: Activity) {
        userReference.document(getCurrentUserID()).get().addOnSuccessListener { document ->
            val loggedUser = document.toObject(User::class.java)!!
            val sharedPreferences = activity.getSharedPreferences(Constants.STOP_N_SHOP_PREFERENCE, Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString(Constants.LOGGED_IN_USERS_NAME, "${loggedUser.firstName} ${loggedUser.lastName}")
            editor.apply()
            when (activity) {
                is LoginActivity -> {
                    userReference.document(getCurrentUserID()).update("status", "Online").addOnSuccessListener {
                        activity.loginSuccess(loggedUser)
                    }.addOnFailureListener { e ->
                        activity.hideProgressDialog()
                        Log.e(activity.javaClass.simpleName, "Error logging in user", e)
                        Toast.makeText(activity, "Error logging in user. Please try again!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }.addOnFailureListener { e ->
            when (activity) {
                is LoginActivity -> {
                    activity.hideProgressDialog()
                }
            }
            Log.e("LogInUser", "Error writing document", e)
        }
    }

    fun logoutUser(activity: Activity) {
        userReference.document(getCurrentUserID()).update("status", "Offline").addOnSuccessListener {
            FirebaseAuth.getInstance().signOut()
            when (activity) {
                is RegisterActivity -> {
                    activity.successfulLogout()
                }
            }
        }.addOnFailureListener { e ->
            Log.e(activity.javaClass.simpleName, "Error logging out user", e)
            Toast.makeText(activity, "Sorry, we can't log you out!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }
}