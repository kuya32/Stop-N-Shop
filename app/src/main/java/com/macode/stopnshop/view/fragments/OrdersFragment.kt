package com.macode.stopnshop.view.fragments

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.FragmentDashboardBinding
import com.macode.stopnshop.databinding.FragmentOrdersBinding
import com.macode.stopnshop.model.Order
import com.macode.stopnshop.view.activities.BaseActivity
import com.macode.stopnshop.view.adapters.OrderListAdapter
import com.macode.stopnshop.viewmodel.ui.notifications.NotificationsViewModel

class OrdersFragment : BaseFragment() {

    private var binding: FragmentOrdersBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrdersBinding.inflate(inflater, container, false)
        val view = binding!!.root

        setUpToolbar(view)

        return view
    }

    override fun onResume() {
        super.onResume()
        getMyOrdersList()
    }

    private fun setUpToolbar(view: View) {
        val toolbar = view.findViewById<Toolbar>(R.id.ordersToolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = resources.getString(R.string.orders)
        (requireActivity() as AppCompatActivity).supportActionBar?.setBackgroundDrawable(
            ContextCompat.getDrawable(requireActivity(), R.drawable.gradient_background))
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"))
    }

    private fun getMyOrdersList() {
        showProgressDialog("Retrieving user order list...")
        fireStoreClass.getMyOrdersList(this@OrdersFragment)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun showAlertDialogToDeleteOrder(id: String, title: String) {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater: LayoutInflater = LayoutInflater.from(requireContext())
        val view: View = inflater.inflate(R.layout.alert_dialog_title, null)
        builder.setCustomTitle(view)
        builder.setMessage("Are you sure you want to delete $title?")
        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            dialogInterface.dismiss()
            fireStoreClass.deleteOrder(this@OrdersFragment, id)
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

    fun populateOrdersListInUI(orderList: ArrayList<Order>) {
        hideProgressDialog()

        orderItemList = orderList
        if (orderItemList.size > 0) {
            binding!!.noOrdersFound.visibility = View.GONE
            binding!!.ordersRV.visibility = View.VISIBLE

            binding!!.ordersRV.layoutManager = LinearLayoutManager(activity)
            binding!!.ordersRV.setHasFixedSize(true)
            val orderAdapter = OrderListAdapter(requireActivity(), orderItemList, this@OrdersFragment)
            binding!!.ordersRV.adapter = orderAdapter
        } else {
            binding!!.noOrdersFound.visibility = View.VISIBLE
            binding!!.ordersRV.visibility = View.GONE
        }
    }

    fun deleteOrderSuccess() {
        hideProgressDialog()
        showErrorSnackBar("Order item successfully deleted!", false)
        getMyOrdersList()
    }
}