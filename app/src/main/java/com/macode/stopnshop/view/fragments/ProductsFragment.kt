package com.macode.stopnshop.view.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.FragmentProductsBinding
import com.macode.stopnshop.model.Product
import com.macode.stopnshop.view.activities.AddEditProductActivity
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
                startActivity(Intent(activity, AddEditProductActivity::class.java))
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
            val productAdapter = MyProductsListAdapter(requireActivity(), productsList, this@ProductsFragment)
            binding!!.productItemsRecyclerView.adapter = productAdapter
        } else {
            binding!!.productItemsRecyclerView.visibility = View.GONE
            binding!!.noProductsAvailable.visibility = View.VISIBLE
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun showAlertDialogToDeleteProduct(id: String, title: String) {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater: LayoutInflater = LayoutInflater.from(requireContext())
        val view: View = inflater.inflate(R.layout.alert_dialog_title, null)
        builder.setCustomTitle(view)
        builder.setMessage("Are you sure you want to delete $title")
        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            dialogInterface.dismiss()
            fireStoreClass.deleteProduct(this@ProductsFragment, id)
        }
        builder.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
        val messageView: TextView = alertDialog.requireViewById(android.R.id.message)
        messageView.gravity = Gravity.CENTER
        val positiveButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
        val negativeButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)
        positiveButton.setTextColor(Color.BLACK)
        negativeButton.setTextColor(Color.BLACK)
        val positiveButtonLinearLayout: LinearLayout.LayoutParams = positiveButton.layoutParams as LinearLayout.LayoutParams
        positiveButtonLinearLayout.weight = 10.0f
        positiveButton.layoutParams = positiveButtonLinearLayout
        negativeButton.layoutParams = positiveButtonLinearLayout
    }

    fun deleteProductSuccess() {
        hideProgressDialog()
        showErrorSnackBar("Product deleted successfully", false)
        getProductsListFromFireStore()
    }

}