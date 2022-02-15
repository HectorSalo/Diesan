package com.skysam.hchirinos.diesan.ui.lots

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.diesan.R
import com.skysam.hchirinos.diesan.common.dataClass.Lot
import com.skysam.hchirinos.diesan.databinding.FragmentLotsBinding
import com.skysam.hchirinos.diesan.ui.MainViewModel
import com.skysam.hchirinos.diesan.ui.settings.SettingsActivity

class LotsFragment : Fragment(), SearchView.OnQueryTextListener, LotOnClick {

    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentLotsBinding? = null
    private val binding get() = _binding!!
    private lateinit var itemSearch: SearchView
    private val lots = mutableListOf<Lot>()
    private lateinit var lotsAdapter: LotsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLotsBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lotsAdapter = LotsAdapter(lots, this)
        binding.rvLots.apply {
            setHasFixedSize(true)
            adapter = lotsAdapter
        }
        loadViewModel()
    }

    private fun loadViewModel() {
        viewModel.lots.observe(viewLifecycleOwner) {
            if (_binding != null) {
                if (it.isNotEmpty()) {
                    lots.clear()
                    lots.addAll(it)
                    lotsAdapter.notifyItemRangeInserted(0, lots.size)
                    binding.rvLots.visibility = View.VISIBLE
                    binding.tvListEmpty.visibility = View.GONE
                } else {
                    binding.rvLots.visibility = View.GONE
                    binding.tvListEmpty.visibility = View.VISIBLE
                }
                binding.progressBar.visibility = View.GONE
            }
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
        val item = menu.findItem(R.id.action_search)
        itemSearch = item.actionView as SearchView
        itemSearch.setOnQueryTextListener(this)
        val itemSettings = menu.findItem(R.id.action_settings)
        itemSettings.setOnMenuItemClickListener {
            requireActivity().startActivity(Intent(requireContext(), SettingsActivity::class.java))
            true
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    override fun viewLot(lot: Lot) {
        viewModel.viewLot(lot)
        val viewDetailsLotDialog = ViewDetailsLotDialog()
        viewDetailsLotDialog.show(requireActivity().supportFragmentManager, tag)
    }
}