package com.skysam.hchirinos.diesan.ui.stock

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.diesan.R
import com.skysam.hchirinos.diesan.common.Class
import com.skysam.hchirinos.diesan.common.dataClass.Lot
import com.skysam.hchirinos.diesan.common.dataClass.Product
import com.skysam.hchirinos.diesan.databinding.DialogViewDetailLotBinding

/**
 * Created by Hector Chirinos on 15/02/2022.
 */
class ViewDetailsStockDialog: DialogFragment() {
    private var _binding: DialogViewDetailLotBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StockViewModel by activityViewModels()
    private lateinit var adapterItems: ItemDetailsStockAdapter
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
        adapterItems = ItemDetailsStockAdapter(products)
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
        viewModel.lotToView.observe(viewLifecycleOwner) {
            if (_binding != null) {
                lot = it
                products.clear()
                products.addAll(lot.products)
                var total = 0.0
                for (pro in products) {
                    total += (pro.priceToSell * pro.quantity)
                }
                adapterItems.notifyItemRangeInserted(0, products.size)
                binding.tvTotal.text = getString(
                    R.string.text_total_dolar,
                    Class.convertDoubleToString(total)
                )
            }
        }
    }
}