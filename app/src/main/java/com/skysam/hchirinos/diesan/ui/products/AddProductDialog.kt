package com.skysam.hchirinos.diesan.ui.products

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
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

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            goGallery()
        } else {
            Toast.makeText(requireContext(), getString(R.string.txt_error_permiso_lectura), Toast.LENGTH_SHORT).show()
        }
    }

    private val requestIntentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            showImage(result.data!!)
        }
    }

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

        binding.ivImage.setOnClickListener { requestPermission() }
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

    private fun requestPermission() {
        if (checkPermission()) {
            goGallery()
            return
        }
        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun goGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        requestIntentLauncher.launch(intent)
    }

    private fun showImage(it: Intent) {
        val url = it.dataString
        val sizeImagePreview = resources.getDimensionPixelSize(R.dimen.size_image_dialog_add_product)
        val bitmap = Class.reduceBitmap(url, sizeImagePreview, sizeImagePreview)

        if (bitmap != null) {
            binding.ivImage.setImageBitmap(bitmap)
        }
    }
}