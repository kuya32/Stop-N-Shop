package com.macode.stopnshop.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.macode.stopnshop.databinding.ActivityCartListBinding

class CartListActivity : BaseActivity() {

    private var binding: ActivityCartListBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartListBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
    }
}