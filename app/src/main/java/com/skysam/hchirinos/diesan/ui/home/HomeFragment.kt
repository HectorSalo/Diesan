package com.skysam.hchirinos.diesan.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.diesan.R
import com.skysam.hchirinos.diesan.databinding.FragmentHomeBinding
import com.skysam.hchirinos.diesan.ui.MainViewModel
import com.skysam.hchirinos.diesan.ui.lots.NewLotActivity
import com.skysam.hchirinos.diesan.ui.settings.SettingsActivity
import com.skysam.hchirinos.diesan.ui.stats.StatsActivity
import com.skysam.hchirinos.diesan.ui.stock.StockActivity

class HomeFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.main, menu)
        val itemSearch = menu.findItem(R.id.action_search)
        itemSearch.isVisible = false
        val itemSettings = menu.findItem(R.id.action_settings)
        itemSettings.setOnMenuItemClickListener {
            requireActivity().startActivity(Intent(requireContext(), SettingsActivity::class.java))
            true
        }
    }
}