package com.skysam.hchirinos.diesan.ui.stock

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.diesan.common.dataClass.Lot
import com.skysam.hchirinos.diesan.databinding.FragmentStockBinding
import com.skysam.hchirinos.diesan.ui.lots.ViewDetailsLotDialog


class StockFragment : Fragment(), StockOnClick {
    private var _binding: FragmentStockBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StockViewModel by activityViewModels()
    private lateinit var stockAdapter: StockAdapter
    private val lots = mutableListOf<Lot>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStockBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                getOut()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        stockAdapter = StockAdapter(lots, this)
        binding.rvStock.apply {
            setHasFixedSize(true)
            adapter = stockAdapter
        }
        loadViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                getOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun loadViewModel() {
        viewModel.lots.observe(viewLifecycleOwner) {
            if (_binding != null) {
                if (it.isNotEmpty()) {
                    lots.clear()
                    lots.addAll(it)
                    stockAdapter.notifyItemRangeInserted(0, lots.size)
                    binding.rvStock.visibility = View.VISIBLE
                    binding.tvListEmpty.visibility = View.GONE
                } else {
                    binding.rvStock.visibility = View.GONE
                    binding.tvListEmpty.visibility = View.VISIBLE
                }
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun getOut() {
        requireActivity().finish()
    }

    override fun viewDetail(lot: Lot) {
        viewModel.viewLot(lot)
        val viewDetailsLotDialog = ViewDetailsLotDialog()
        viewDetailsLotDialog.show(requireActivity().supportFragmentManager, tag)
    }

    override fun sell(lot: Lot) {
        TODO("Not yet implemented")
    }

    override fun share(lot: Lot) {
        TODO("Not yet implemented")
    }
}