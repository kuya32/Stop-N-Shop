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
import com.macode.stopnshop.databinding.FragmentProductsBinding
import com.macode.stopnshop.view.activities.BaseActivity
import com.macode.stopnshop.viewmodel.ui.dashboard.DashboardViewModel

class DashboardFragment : Fragment() {

    private var binding: FragmentDashboardBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val view = binding!!.root

        setUpToolbar(view)

        binding!!.textDashboard.text = "This is the dashboard fragment"

        return view
    }

    private fun setUpToolbar(view: View) {
        val toolbar = view.findViewById<Toolbar>(R.id.dashboardFragmentToolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = resources.getString(R.string.title_dashboard)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (requireActivity() as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.back_white)
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"))
        toolbar.setNavigationOnClickListener {
            (requireActivity() as BaseActivity).doubleBackToExit()
        }
    }
}