package com.macode.stopnshop.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.macode.stopnshop.databinding.ActivityProductDetailBinding

class ProductDetailActivity : BaseActivity() {

    private var binding: ActivityProductDetailBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
    }
}