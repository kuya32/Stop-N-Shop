package com.macode.stopnshop.view.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.FragmentOrdersBinding
import com.macode.stopnshop.databinding.FragmentSoldProductsBinding
import com.macode.stopnshop.model.SoldProduct
import com.macode.stopnshop.view.adapters.SoldProductListAdapter

class SoldProductsFragment : BaseFragment() {

    private var binding: FragmentSoldProductsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSoldProductsBinding.inflate(inflater, container, false)
        val view = binding!!.root

        setUpToolbar(view)

        return view
    }

    override fun onResume() {
        super.onResume()
        getSoldProductList()
    }

    private fun setUpToolbar(view: View) {
        val toolbar = view.findViewById<Toolbar>(R.id.soldProductsToolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = resources.getString(R.string.soldProducts)
        (requireActivity() as AppCompatActivity).supportActionBar?.setBackgroundDrawable(
            ContextCompat.getDrawable(requireActivity(), R.drawable.gradient_background))
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"))
    }

    private fun getSoldProductList() {
        showProgressDialog("Retrieving sold product list...")
        fireStoreClass.getSoldProductsList(this@SoldProductsFragment)
    }

    fun soldProductListRetrievalSuccess(soldProductList: ArrayList<SoldProduct>) {
        hideProgressDialog()
        if (soldProductList.size > 0) {
            binding!!.noProductSold.visibility = View.GONE
            binding!!.soldProductsRV.visibility = View.VISIBLE

            binding!!.soldProductsRV.layoutManager = LinearLayoutManager(activity)
            binding!!.soldProductsRV.setHasFixedSize(true)
            val soldProductAdapter = SoldProductListAdapter(requireActivity(), soldProductList, this@SoldProductsFragment)
            binding!!.soldProductsRV.adapter = soldProductAdapter
        } else {
            binding!!.noProductSold.visibility = View.VISIBLE
            binding!!.soldProductsRV.visibility = View.GONE
        }
    }
}