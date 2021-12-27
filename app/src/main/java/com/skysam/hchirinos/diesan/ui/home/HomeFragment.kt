package com.skysam.hchirinos.diesan.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.diesan.databinding.FragmentHomeBinding
import com.skysam.hchirinos.diesan.ui.lots.NewLotActivity
import com.skysam.hchirinos.diesan.ui.stats.StatsActivity
import com.skysam.hchirinos.diesan.ui.stock.StockActivity

class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by activityViewModels()
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnNewLot.setOnClickListener {
            startActivity(Intent(requireContext(), NewLotActivity::class.java))
        }
        binding.btnStats.setOnClickListener {
            startActivity(Intent(requireContext(), StatsActivity::class.java))
        }
        binding.btnStock.setOnClickListener {
            startActivity(Intent(requireContext(), StockActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}