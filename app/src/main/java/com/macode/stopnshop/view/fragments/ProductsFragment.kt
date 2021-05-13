package com.macode.stopnshop.view.fragments

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.FragmentProductsBinding
import com.macode.stopnshop.firebase.FireStoreClass
import com.macode.stopnshop.model.Product
import com.macode.stopnshop.view.activities.AddProductActivity
import com.macode.stopnshop.view.activities.BaseActivity
import com.macode.stopnshop.view.adapters.MyProductsListAdapter

class ProductsFragment : BaseFragment() {

    private var binding: FragmentProductsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductsBinding.inflate(inflater, container, false)
        val view = binding!!.root

        setUpToolbar(view)

        return view
    }

    override fun onResume() {
        super.onResume()
        getProductsListFromFireStore()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_product_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_product_action -> {
                startActivity(Intent(activity, AddProductActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpToolbar(view: View) {
        val toolbar = view.findViewById<Toolbar>(R.id.productsToolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = resources.getString(R.string.products)
        (requireActivity() as AppCompatActivity).supportActionBar?.setBackgroundDrawable(
            ContextCompat.getDrawable(requireActivity(), R.drawable.gradient_background))
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"))
    }

    private fun getProductsListFromFireStore() {
        showProgressDialog("Retrieving user product items...")
        fireStoreClass.getProductsList(this@ProductsFragment)
    }

    fun successProductsListFromFireStore(productsList: ArrayList<Product>) {
        hideProgressDialog()

        if (productsList.size > 0) {
            binding!!.noProductsAvailable.visibility = View.GONE
            binding!!.productItemsRecyclerView.visibility = View.VISIBLE

            binding!!.productItemsRecyclerView.layoutManager = LinearLayoutManager(activity)
            binding!!.productItemsRecyclerView.setHasFixedSize(true)
            val productAdapter = MyProductsListAdapter(requireActivity(), productsList)
            binding!!.productItemsRecyclerView.adapter = productAdapter
        } else {
            binding!!.productItemsRecyclerView.visibility = View.GONE
            binding!!.noProductsAvailable.visibility = View.VISIBLE
        }
    }
}