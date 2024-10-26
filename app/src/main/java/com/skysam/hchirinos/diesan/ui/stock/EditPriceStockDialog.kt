package com.skysam.hchirinos.diesan.ui.stock

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.diesan.R
import com.skysam.hchirinos.diesan.common.Class
import com.skysam.hchirinos.diesan.common.dataClass.Lot
import com.skysam.hchirinos.diesan.common.dataClass.Product
import com.skysam.hchirinos.diesan.databinding.DialogEditPriceSaleBinding
import java.util.Locale

/**
 * Created by Hector Chirinos on 25/10/2024.
 */

class EditPriceStockDialog: DialogFragment(), TextWatcher {
	private var _binding: DialogEditPriceSaleBinding? = null
	private val binding get() = _binding!!
	private val viewModel: StockViewModel by activityViewModels()
	private lateinit var product: Product
	private lateinit var buttonPositive: Button
	private lateinit var buttonNegative: Button
	private val lots = mutableListOf<Lot>()
	private var updating = false
	private var newPrice = 0.0
	
	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		_binding = DialogEditPriceSaleBinding.inflate(layoutInflater)
		
		
		
		val builder = AlertDialog.Builder(requireActivity())
		builder.setTitle(getString(R.string.title_edit_price_dialog))
			.setView(binding.root)
			.setPositiveButton(R.string.text_update, null)
			.setNegativeButton(R.string.text_cancel, null)
		
		val dialog = builder.create()
		dialog.show()
		
		buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
		buttonNegative.setOnClickListener { dialog.dismiss() }
		buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
		buttonPositive.setOnClickListener { validateData() }
		
		binding.etPrice.addTextChangedListener(this)
		loadViewModel()
		
		return dialog
	}
	
	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
	
	private fun loadViewModel() {
		viewModel.productToChangePrice.observe(this.requireActivity()) {
			if (_binding != null) {
				product = it
				binding.tvPriceBase.text = getString(R.string.text_item_price,
					Class.convertDoubleToString(product.priceByUnit))
				binding.tvProfitUnit.text = getString(R.string.text_item_price,
					Class.convertDoubleToString(product.amountProfit))
				binding.textView11.text = "Porcentaje de ganancia"
				binding.tvProfitTotal.text = "${Class.convertDoubleToString(((product.priceToSell - product.priceByUnit) * 100) / product.priceByUnit)}%"
				binding.etPrice.setText(Class.convertDoubleToString(product.priceToSell))
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
	
	override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
	
	override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
	
	override fun afterTextChanged(s: Editable?) {
		var cadena = s.toString()
		cadena = cadena.replace(",", "").replace(".", "")
		if (cadena.isNotEmpty()) {
			val cantidad: Double = cadena.toDouble() / 100
			cadena = String.format(Locale.GERMANY, "%,.2f", cantidad)
			
			if (s.toString() == binding.etPrice.text.toString()) {
				binding.etPrice.removeTextChangedListener(this)
				binding.etPrice.setText(cadena)
				binding.etPrice.setSelection(cadena.length)
				binding.etPrice.addTextChangedListener(this)
				
				newPrice = cantidad
				binding.tvProfitUnit.text = getString(R.string.text_item_price,
					Class.convertDoubleToString(cantidad - product.priceByUnit))
				binding.tvProfitTotal.text = "${Class.convertDoubleToString(((cantidad - product.priceByUnit) * 100) / product.priceByUnit)}%"
			}
		}
	}
	
	private fun validateData() {
		val price = binding.etPrice.text.toString().replace(".", "").replace(",", ".").toDouble()
		
		if (price == 0.00) return
		
		updating = true
		product.priceToSell = newPrice
		product.amountProfit = newPrice - product.priceByUnit
		
		lots.forEach {
			it.products.forEach { pr ->
				if (product.name == pr.name && product.priceByUnit == pr.priceByUnit
					&& product.percentageProfit == pr.percentageProfit) {
					pr.priceToSell = newPrice
					pr.amountProfit = newPrice - product.priceByUnit
					pr.percentageProfit = ((pr.priceToSell - pr.priceByUnit) * 100) / pr.priceByUnit
				}
			}
			viewModel.updateStock(it)
		}
		dismiss()
	}
}