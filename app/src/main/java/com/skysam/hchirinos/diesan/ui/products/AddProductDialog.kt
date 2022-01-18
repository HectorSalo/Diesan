package com.skysam.hchirinos.diesan.ui.products

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import com.skysam.hchirinos.diesan.R
import com.skysam.hchirinos.diesan.common.Class
import com.skysam.hchirinos.diesan.common.Constants
import com.skysam.hchirinos.diesan.common.dataClass.Product
import com.skysam.hchirinos.diesan.databinding.DialogAddProductBinding

/**
 * Created by Hector Chirinos (Home) on 28/12/2021.
 */
class AddProductDialog(private val products: MutableList<Product>): DialogFragment() {
    private var _binding: DialogAddProductBinding? = null
    private val binding get() = _binding!!
    private lateinit var buttonPositive: Button
    private lateinit var buttonNegative: Button
    private lateinit var image: String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddProductBinding.inflate(layoutInflater)

        binding.etName.doAfterTextChanged { binding.tfName.error = null }

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_add_producto_dialog))
            .setView(binding.root)
            .setPositiveButton(R.string.text_save, null)
            .setNegativeButton(R.string.text_cancel, null)

        val dialog = builder.create()
        dialog.show()

        buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        buttonNegative.setOnClickListener { dialog.dismiss() }
        buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonPositive.setOnClickListener { validateData() }

        binding.ivImage.setOnClickListener { chooseImage() }
        return dialog
    }

    private fun validateData() {
        binding.tfName.error = null
        val name = binding.etName.text.toString().trim()
        if (name.isEmpty()) {
            binding.tfName.error = getString(R.string.error_field_empty)
            binding.etName.requestFocus()
            return
        }
        for (prod in products) {
            if (prod.name.equals(name, true)) {
                binding.tfName.error = getString(R.string.error_name_exists)
                binding.etName.requestFocus()
                return
            }
        }
        Class.keyboardClose(binding.root)

        val product = Product(
            Constants.ID,
            name,
            ship = 0.0,
            tax = 0.0,
            sumTotal = 0.0,
            priceByUnit = 0.0,
            percentageProfit = 0.0,
            priceToSell = 0.0,
            amountProfit = 0.0,
            image = image
        )
        dialog?.dismiss()
    }

    private fun chooseImage() {

    }
}