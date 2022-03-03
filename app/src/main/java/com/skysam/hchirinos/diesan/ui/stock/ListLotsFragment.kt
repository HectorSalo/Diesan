package com.skysam.hchirinos.diesan.ui.stock

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
import com.skysam.hchirinos.diesan.common.dataClass.Lot
import com.skysam.hchirinos.diesan.databinding.FragmentListLotsBinding
import com.skysam.hchirinos.diesan.ui.common.WrapContentLinearLayoutManager

class ListLotsFragment : Fragment(), StockOnClick {
	private var _binding: FragmentListLotsBinding? = null
	private val binding get() = _binding!!
	private val viewModel: StockViewModel by activityViewModels()
	private lateinit var stockAdapter: StockAdapter
	private lateinit var wrapContentLinearLayoutManager: WrapContentLinearLayoutManager
	private val lots = mutableListOf<Lot>()
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentListLotsBinding.inflate(inflater, container, false)
		setHasOptionsMenu(true)
		return binding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val callback = object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				getBack()
			}
		}
		requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
		wrapContentLinearLayoutManager = WrapContentLinearLayoutManager(requireContext(),
			RecyclerView.VERTICAL, false)
		stockAdapter = StockAdapter(lots, this)
		binding.rvListStock.apply {
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
				getBack()
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
					binding.rvListStock.visibility = View.VISIBLE
					binding.tvListEmpty.visibility = View.GONE
				} else {
					binding.rvListStock.visibility = View.GONE
					binding.tvListEmpty.visibility = View.VISIBLE
				}
				binding.progressBar.visibility = View.GONE
			}
		}
	}
	
	private fun getBack() {
		findNavController().navigate(R.id.action_listLotsFragment_to_FragmentStock)
	}
	override fun viewDetail(lot: Lot) {
		viewModel.viewLot(lot)
		val viewDetailsStockDialog = ViewDetailsStockDialog()
		viewDetailsStockDialog.show(requireActivity().supportFragmentManager, tag)
	}
}