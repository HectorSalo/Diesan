package com.skysam.hchirinos.diesan.ui.sales

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
import com.skysam.hchirinos.diesan.common.dataClass.Product
import com.skysam.hchirinos.diesan.databinding.DialogEditPriceSaleBinding
import java.util.*

/**
 * Created by Hector Chirinos on 26/10/2022.
 */

class EditPriceDialog: DialogFragment(), TextWatcher {
	private var _binding: DialogEditPriceSaleBinding? = null
	private val binding get() = _binding!!
	private val viewModel: NewSaleViewModel by activityViewModels()
	private lateinit var product: Product
	private lateinit var buttonPositive: Button
	private lateinit var buttonNegative: Button
	private var newPrice = 0.0
	
	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		_binding = DialogEditPriceSaleBinding.inflate(layoutInflater)
		
		binding.etPrice.addTextChangedListener(this)
		
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
		
		loadViewModel()
		
		return dialog
	}
	
	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
	
	private fun validateData() {
		val amountProfit = newPrice - product.priceByUnit
		val percentage = if (amountProfit != 0.0) amountProfit / product.priceByUnit else 0.0
		val productEdited = Product(
			product.id,
			product.name,
			product.price,
			product.quantity,
			product.ship,
			product.tax,
			product.sumTotal,
			product.priceByUnit,
			percentage,
			newPrice,
			amountProfit,
			product.image,
			product.isCheck
		)
		viewModel.editProductToSell(productEdited)
		dismiss()
	}
	
	private fun loadViewModel() {
		viewModel.productToChangePrice.observe(this.requireActivity()) {
			if (_binding != null) {
				product = it
				binding.tvPriceBase.text = getString(R.string.text_item_price,
					Class.convertDoubleToString(product.priceByUnit))
				binding.tvProfitUnit.text = getString(R.string.text_item_price,
					Class.convertDoubleToString(product.amountProfit))
				binding.tvProfitTotal.text = getString(R.string.text_item_price,
					Class.convertDoubleToString(product.amountProfit * product.quantity))
				binding.etPrice.setText(Class.convertDoubleToString(product.priceToSell))
			}
		}
	}
	
	override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
	
	}
	
	override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
	
	}
	
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
				binding.tvProfitTotal.text = getString(R.string.text_item_price,
					Class.convertDoubleToString((cantidad - product.priceByUnit) * product.quantity))
			}
		}
	}
}