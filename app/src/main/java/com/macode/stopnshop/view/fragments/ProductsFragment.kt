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
import com.macode.stopnshop.databinding.FragmentProductsBinding
import com.macode.stopnshop.view.activities.BaseActivity

class ProductsFragment : Fragment() {

    private var binding: FragmentProductsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductsBinding.inflate(inflater, container, false)
        val view = binding!!.root

        setUpToolbar(view)

        binding!!.textProduct.text = "This is product fragment"

        return view
    }

    private fun setUpToolbar(view: View) {
        val toolbar = view.findViewById<Toolbar>(R.id.productsToolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = resources.getString(R.string.products)
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"))
    }
}