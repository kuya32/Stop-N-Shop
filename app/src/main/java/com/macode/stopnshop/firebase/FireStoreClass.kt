package com.macode.stopnshop.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.macode.stopnshop.model.User
import com.macode.stopnshop.view.activities.RegisterActivity

class FireStoreClass {
    private val fireStore = FirebaseFirestore.getInstance()
    private val userReference = fireStore.collection("Users")

    fun registerUser(activity: RegisterActivity, userInfo: User) {
        userReference.document()
    }

    fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
    }
}