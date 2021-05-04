package com.macode.stopnshop.view.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.FragmentHomeBinding
import com.macode.stopnshop.view.activities.BaseActivity

class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding!!.root

        setUpToolbar(view)

        binding!!.textHome.text = "This is home fragment"

        return view
    }

    private fun setUpToolbar(view: View) {
        val toolbar = view.findViewById<Toolbar>(R.id.homeToolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Home"
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (requireActivity() as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.back_white)
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"))
        toolbar.setNavigationOnClickListener {
            (requireActivity() as BaseActivity).doubleBackToExit()
        }
    }
}