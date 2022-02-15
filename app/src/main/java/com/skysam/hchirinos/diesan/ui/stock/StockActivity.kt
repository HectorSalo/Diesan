package com.skysam.hchirinos.diesan.ui.stock

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.skysam.hchirinos.diesan.databinding.ActivityStockBinding

class StockActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStockBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}