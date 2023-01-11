package com.skysam.hchirinos.diesan.ui.sales

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.skysam.hchirinos.diesan.databinding.ActivityAddSaleBinding

class AddSaleActivity : AppCompatActivity() {
	private lateinit var binding: ActivityAddSaleBinding
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityAddSaleBinding.inflate(layoutInflater)
		setContentView(binding.root)
		setSupportActionBar(binding.toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
	}
}