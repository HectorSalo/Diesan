package com.skysam.hchirinos.diesan.ui.lots

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.diesan.R
import com.skysam.hchirinos.diesan.common.Class
import com.skysam.hchirinos.diesan.common.dataClass.Lot
import com.skysam.hchirinos.diesan.common.dataClass.Product
import com.skysam.hchirinos.diesan.databinding.DialogViewDetailLotBinding
import com.skysam.hchirinos.diesan.ui.MainViewModel

/**
 * Created by Hector Chirinos on 14/02/2022.
 */
class ViewDetailsLotDialog: DialogFragment() {
    private var _binding: DialogViewDetailLotBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapterItems: ItemsDetailsNewLotAdapter
    private val products = mutableListOf<Product>()
    private lateinit var lot: Lot

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
        adapterItems = ItemsDetailsNewLotAdapter(products) { position, product ->
            showRenameDialog(position, product)
        }
        binding.rvProducts.apply {
            setHasFixedSize(true)
            adapter = adapterItems
        }

        loadViewModel()
    }

    private fun showRenameDialog(position: Int, product: Product) {
        if (!::lot.isInitialized) return

        val hasProductKey = !product.productKey.isNullOrBlank()

        val editText = EditText(requireContext()).apply {
            setText(product.name)
            setSelection(text.length)
            hint = getString(R.string.hint_rename_product_lot)
        }

        val syncCheckBox = if (hasProductKey) {
            CheckBox(requireContext()).apply {
                text = getString(R.string.checkbox_rename_sync_stock_catalog)
                isChecked = true
            }
        } else null

        val pad = (16 * resources.displayMetrics.density).toInt()
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad / 2, pad, pad / 2)
            addView(editText)
            syncCheckBox?.let { addView(it) }
        }

        val messageRes = if (hasProductKey) {
            R.string.msg_rename_product_lot
        } else {
            R.string.msg_rename_product_lot_no_key
        }

        AlertDialog.Builder(requireActivity())
            .setTitle(R.string.title_rename_product_lot_dialog)
            .setMessage(messageRes)
            .setView(container)
            .setPositiveButton(R.string.text_save) { _, _ ->
                val newName = editText.text.toString().trim()
                if (newName.isEmpty()) {
                    Toast.makeText(requireContext(), R.string.error_field_empty, Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val oldName = product.name
                if (newName == oldName) return@setPositiveButton

                lot.products[position].name = newName
                adapterItems.notifyItemChanged(position)
                if (hasProductKey && syncCheckBox?.isChecked == true) {
                    viewModel.renameProductInLotStockAndCatalog(lot, position, oldName, newName)
                } else {
                    viewModel.renameProductInLot(lot, position, oldName, newName)
                }
            }
            .setNegativeButton(R.string.text_cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getOut() {
        dismiss()
    }

    private fun loadViewModel() {
        viewModel.lotToView.observe(viewLifecycleOwner) {
            if (_binding != null) {
                lot = it
                products.clear()
                products.addAll(lot.products)
                adapterItems.notifyItemRangeInserted(0, products.size)
                binding.tvTotal.text = getString(
                    R.string.text_total_dolar,
                    Class.convertDoubleToString(it.ship)
                )
            }
        }
    }
}