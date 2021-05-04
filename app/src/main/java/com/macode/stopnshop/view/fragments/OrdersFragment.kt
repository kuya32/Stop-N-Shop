package com.macode.stopnshop.view.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.FragmentDashboardBinding
import com.macode.stopnshop.databinding.FragmentOrdersBinding
import com.macode.stopnshop.view.activities.BaseActivity
import com.macode.stopnshop.viewmodel.ui.notifications.NotificationsViewModel

class OrdersFragment : Fragment() {

    private var binding: FragmentOrdersBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrdersBinding.inflate(inflater, container, false)
        val view = binding!!.root

        setUpToolbar(view)

        binding!!.textNotifications.text = "This is the orders fragment"

        return view
    }

    private fun setUpToolbar(view: View) {
        val toolbar = view.findViewById<Toolbar>(R.id.ordersToolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = resources.getString(R.string.orders)
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"))
    }
}