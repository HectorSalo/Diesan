package com.skysam.hchirinos.diesan.ui.lots

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.skysam.hchirinos.diesan.R
import com.skysam.hchirinos.diesan.databinding.ActivityNewLotBinding

class NewLotActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewLotBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewLotBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}