package com.macode.stopnshop.view.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.ActivityDashboardBinding
import com.macode.stopnshop.utilities.SNSTextView

class DashboardActivity : BaseActivity() {

    private var binding: ActivityDashboardBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        setupToolbar()

        binding!!.goToCartFab.setOnClickListener {
            startActivity(Intent(this@DashboardActivity, CartListActivity::class.java))
        }

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dashboard, R.id.navigation_products, R.id.navigation_orders
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onResume() {
        super.onResume()
        fireStoreClass.checkIfItemExistsInCart(this@DashboardActivity)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.dashboardToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        toolbar.title = "Stop N Shop"
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFFFF"))
    }

    override fun onBackPressed() {
        doubleBackToExit()
    }

    fun itemsFoundInCart() {
        binding!!.goToCartFab.visibility = View.VISIBLE
    }

    fun hideCartFabButton() {
        binding!!.goToCartFab.visibility = View.GONE
    }
}