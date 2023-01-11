package com.skysam.hchirinos.diesan.ui.lots

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.skysam.hchirinos.diesan.databinding.ActivityNewLotBinding

class NewLotActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewLotBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewLotBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}