package com.skysam.hchirinos.diesan.ui.lots

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
import com.skysam.hchirinos.diesan.ui.MainViewModel
import com.skysam.hchirinos.diesan.ui.stock.StockViewModel

/**
 * Created by Hector Chirinos on 14/02/2022.
 */
class ViewDetailsLotDialog: DialogFragment() {
    private var _binding: DialogViewDetailLotBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private val viewModel2: StockViewModel by activityViewModels()
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
        adapterItems = ItemsDetailsNewLotAdapter(products)
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
                var totalProfit = 0.0
                for (pro in products) {
                    totalProfit += pro.amountProfit
                }
                adapterItems.notifyItemRangeInserted(0, products.size)
                binding.tvTotal.text = getString(
                    R.string.text_total_dolar,
                    Class.convertDoubleToString(totalProfit)
                )
            }
        }
        viewModel2.lotToView.observe(viewLifecycleOwner) {
            if (_binding != null) {
                lot = it
                products.clear()
                products.addAll(lot.products)
                var totalProfit = 0.0
                for (pro in products) {
                    totalProfit += pro.amountProfit
                }
                adapterItems.notifyItemRangeInserted(0, products.size)
                binding.tvTotal.text = getString(
                    R.string.text_total_dolar,
                    Class.convertDoubleToString(totalProfit)
                )
            }
        }
    }
}