package com.skysam.hchirinos.diesan.ui.stock

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.skysam.hchirinos.diesan.R
import com.skysam.hchirinos.diesan.common.Class
import com.skysam.hchirinos.diesan.common.dataClass.Lot
import com.skysam.hchirinos.diesan.databinding.FragmentStockBinding
import com.skysam.hchirinos.diesan.ui.common.WrapContentLinearLayoutManager


class StockFragment : Fragment(), StockOnClick {
    private var _binding: FragmentStockBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StockViewModel by activityViewModels()
    private lateinit var stockAdapter: StockAdapter
    private lateinit var wrapContentLinearLayoutManager: WrapContentLinearLayoutManager
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
        wrapContentLinearLayoutManager = WrapContentLinearLayoutManager(requireContext(),
            RecyclerView.VERTICAL, false)
        stockAdapter = StockAdapter(lots, this)
        binding.rvStock.apply {
            setHasFixedSize(true)
            adapter = stockAdapter
            layoutManager = wrapContentLinearLayoutManager
        }
        loadViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
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
        val viewDetailsStockDialog = ViewDetailsStockDialog()
        viewDetailsStockDialog.show(requireActivity().supportFragmentManager, tag)
    }

    override fun sell(lot: Lot) {
        viewModel.lotToSell(lot)
        findNavController().navigate(R.id.action_FragmentStock_to_addSaleFragment)
    }

    override fun share(lot: Lot) {
        val selection = StringBuilder()
        for (item in lot.products) {
            selection.append("\n").append("${item.name}: $${Class.convertDoubleToString(item.priceToSell)}")
        }
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, selection.toString())
        startActivity(Intent.createChooser(intent, getString(R.string.title_share_dialog)))
    }
}