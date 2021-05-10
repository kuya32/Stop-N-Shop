package com.macode.stopnshop.view.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.FragmentEditPasswordBinding
import com.macode.stopnshop.firebase.FireStoreClass


class EditPasswordFragment : Fragment() {

    private var binding: FragmentEditPasswordBinding? = null
    private var firebaseAuth = FirebaseAuth.getInstance()
    private var firebaseUser = firebaseAuth.currentUser
    private var fireStoreClass = FireStoreClass()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditPasswordBinding.inflate(inflater, container, false)
        val view = binding!!.root

        setUpToolbar(view)

        return view
    }

    private fun setUpToolbar(view: View) {
        val toolbar = view.findViewById<Toolbar>(R.id.editPasswordToolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(
            Color.TRANSPARENT))
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Edit Password"
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"))
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (requireActivity() as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.back_white)
        toolbar.setNavigationOnClickListener {
            val main = requireActivity().findViewById<ScrollView>(R.id.settingsMainRelative)
            val secondary = requireActivity().findViewById<RelativeLayout>(R.id.settingsSecondaryRelative)
            secondary.visibility = View.GONE
            main.visibility = View.VISIBLE
        }
    }

}