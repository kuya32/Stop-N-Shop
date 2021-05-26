package com.macode.stopnshop.view.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.FragmentDashboardBinding
import com.macode.stopnshop.model.Product
import com.macode.stopnshop.view.activities.BaseActivity
import com.macode.stopnshop.view.activities.SettingsActivity
import com.macode.stopnshop.view.adapters.DashboardListAdapter

class DashboardFragment : BaseFragment() {

    private var binding: FragmentDashboardBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val view = binding!!.root

        setUpToolbar(view)

        return view
    }

    override fun onResume() {
        super.onResume()
        getDashboardItemsList()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.setting_action -> {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpToolbar(view: View) {
        val toolbar = view.findViewById<Toolbar>(R.id.dashboardFragmentToolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = resources.getString(R.string.title_dashboard)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (requireActivity() as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.back_white)
        (requireActivity() as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.gradient_background))
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"))
        toolbar.setNavigationOnClickListener {
            (requireActivity() as BaseActivity).doubleBackToExit()
        }
    }
    private fun getDashboardItemsList() {
        showProgressDialog("Retrieving all available product items...")
        fireStoreClass.getDashboardList(this@DashboardFragment)
    }

    fun successDashboardListFromFireStore(productList: ArrayList<Product>) {
        hideProgressDialog()

        if (productList.size > 0) {
            binding!!.noProductsAvailableDashboard.visibility = View.GONE
            binding!!.dashboardListRecyclerView.visibility = View.VISIBLE

            binding!!.dashboardListRecyclerView.layoutManager = GridLayoutManager(activity, 2)
            binding!!.dashboardListRecyclerView.setHasFixedSize(true)
            val dashboardAdapter = DashboardListAdapter(requireActivity(), productList)
            binding!!.dashboardListRecyclerView.adapter = dashboardAdapter
        } else {
            binding!!.dashboardListRecyclerView.visibility = View.GONE
            binding!!.noProductsAvailableDashboard.visibility = View.VISIBLE
        }
    }


}