package com.skysam.hchirinos.diesan.ui.stock

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.diesan.R
import com.skysam.hchirinos.diesan.common.dataClass.Lot
import com.skysam.hchirinos.diesan.common.dataClass.Product
import com.skysam.hchirinos.diesan.databinding.DialogUpdateStockBinding

/**
 * Created by Hector Chirinos on 26/10/2022.
 */

class EditStockDialog: DialogFragment() {
	private var _binding: DialogUpdateStockBinding? = null
	private val binding get() = _binding!!
	private val viewModel: StockViewModel by activityViewModels()
	private lateinit var product: Product
	private lateinit var buttonPositive: Button
	private lateinit var buttonNegative: Button
	private val lots = mutableListOf<Lot>()
	private var updating = false
	private var quantityTotal = 0
	
	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		_binding = DialogUpdateStockBinding.inflate(layoutInflater)
		
		binding.etQuantity.doAfterTextChanged { binding.tfQuantity.error = null }
		
		val builder = AlertDialog.Builder(requireActivity())
		builder.setTitle(getString(R.string.title_edit_stock_dialog))
			.setView(binding.root)
			.setPositiveButton(R.string.text_update, null)
			.setNegativeButton(R.string.text_cancel, null)
		
		val dialog = builder.create()
		dialog.show()
		
		binding.ibAddQuantity.setOnClickListener { addQuantity() }
		binding.ibRestQuantity.setOnClickListener { restQuantity() }
		buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
		buttonNegative.setOnClickListener { dialog.dismiss() }
		buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
		buttonPositive.setOnClickListener { validateData() }
		
		loadViewModel()
		
		return dialog
	}
	
	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
	
	private fun validateData() {
		binding.tfQuantity.error = null
		
		if (binding.etQuantity.text.isNullOrEmpty()) return
		
		updating = true
		var quantityToRemove = product.quantity - quantityTotal
		
		for (lot in lots) {
			val productsByLot = mutableListOf<Product>()
			productsByLot.addAll(lot.products)
			for (pr in productsByLot) {
				if (product.name == pr.name && product.priceByUnit == pr.priceByUnit
					&& product.percentageProfit == pr.percentageProfit) {
					val quanityFinal = pr.quantity - quantityToRemove
					if (quanityFinal == 0) {
						lot.products.remove(pr)
						quantityToRemove = 0
					}
					if (quanityFinal < 0) {
						lot.products.remove(pr)
						quantityToRemove = quanityFinal * (-1)
					}
					if (quanityFinal > 0) {
						lot.products[lot.products.indexOf(pr)].quantity = quanityFinal
						quantityToRemove = 0
					}
					break
				}
			}
			viewModel.updateStock(lot)
		}
		
		dismiss()
	}
	
	private fun restQuantity() {
		if (quantityTotal > 0) {
			quantityTotal -= 1
			binding.etQuantity.setText(quantityTotal.toString())
		}
	}
	
	private fun addQuantity() {
		quantityTotal += 1
		binding.etQuantity.setText(quantityTotal.toString())
	}
	
	private fun loadViewModel() {
		viewModel.productToChangeStock.observe(this.requireActivity()) {
			if (_binding != null) {
				product = it
				quantityTotal = product.quantity
				binding.etQuantity.setText(product.quantity.toString())
			}
		}
		viewModel.lots.observe(this.requireActivity()) {
			if (_binding != null) {
				if (!updating) {
					lots.clear()
					lots.addAll(it)
				}
			}
		}
	}
}