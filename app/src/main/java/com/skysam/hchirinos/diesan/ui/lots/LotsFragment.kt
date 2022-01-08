package com.skysam.hchirinos.diesan.ui.lots

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.skysam.hchirinos.diesan.databinding.FragmentLotsBinding

class LotsFragment : Fragment() {

    private lateinit var lotsViewModel: LotsViewModel
    private var _binding: FragmentLotsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lotsViewModel =
            ViewModelProvider(this).get(LotsViewModel::class.java)

        _binding = FragmentLotsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}