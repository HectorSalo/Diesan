package com.skysam.hchirinos.diesan.ui.sales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.diesan.R
import com.skysam.hchirinos.diesan.common.Class
import com.skysam.hchirinos.diesan.common.dataClass.Product
import com.skysam.hchirinos.diesan.common.dataClass.Sale
import com.skysam.hchirinos.diesan.databinding.DialogViewDetailLotBinding
import com.skysam.hchirinos.diesan.ui.MainViewModel

/**
 * Created by Hector Chirinos on 17/02/2022.
 */
class ViewDetailsSaleDialog: DialogFragment() {
	private var _binding: DialogViewDetailLotBinding? = null
	private val binding get() = _binding!!
	private val viewModel: MainViewModel by activityViewModels()
	private lateinit var adapterItems: ItemDetailsSaleAdapter
	private val products = mutableListOf<Product>()
	private lateinit var sale: Sale
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setStyle(
			STYLE_NORMAL,
			R.style.ShapeAppearanceOverlay_MaterialComponents_MaterialCalendar_Window_Fullscreen
		)
	}
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = DialogViewDetailLotBinding.inflate(inflater, container, false)
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
		adapterItems = ItemDetailsSaleAdapter(products)
		binding.rvProducts.apply {
			setHasFixedSize(true)
			adapter = adapterItems
		}
		
		loadViewModel()
	}
	
	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
	
	private fun getOut() {
		dismiss()
	}
	
	private fun loadViewModel() {
		viewModel.saleToView.observe(viewLifecycleOwner) {
			if (_binding != null) {
				sale = it
				products.clear()
				products.addAll(sale.products)
				var totalSale = 0.0
				for (pro in products) {
					totalSale += (pro.priceToSell * pro.quantity)
				}
				adapterItems.notifyItemRangeInserted(0, products.size)
				binding.tvTotal.text = getString(
					R.string.text_total_dolar,
					Class.convertDoubleToString(totalSale)
				)
			}
		}
	}
}