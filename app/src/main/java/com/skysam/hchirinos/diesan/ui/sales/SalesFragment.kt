package com.skysam.hchirinos.diesan.ui.sales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.diesan.common.dataClass.Sale
import com.skysam.hchirinos.diesan.databinding.FragmentSalesBinding
import com.skysam.hchirinos.diesan.ui.MainViewModel


class SalesFragment : Fragment() {
    private var _binding: FragmentSalesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var salesAdapter: SalesAdapter
    private val sales = mutableListOf<Sale>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSalesBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        salesAdapter = SalesAdapter(sales)
        binding.rvSales.apply {
            setHasFixedSize(true)
            adapter = salesAdapter
        }
        loadViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadViewModel() {
        viewModel.sales.observe(viewLifecycleOwner) {
            if (_binding != null) {
                if (it.isNotEmpty()) {
                    sales.clear()
                    sales.addAll(it)
                    salesAdapter.notifyItemRangeInserted(0, sales.size)
                    binding.rvSales.visibility = View.VISIBLE
                    binding.tvListEmpty.visibility = View.GONE
                } else {
                    binding.rvSales.visibility = View.GONE
                    binding.tvListEmpty.visibility = View.VISIBLE
                }
                binding.progressBar.visibility = View.GONE
            }
        }
    }
}