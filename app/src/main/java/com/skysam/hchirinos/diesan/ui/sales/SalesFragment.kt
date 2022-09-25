package com.skysam.hchirinos.diesan.ui.sales

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.skysam.hchirinos.diesan.R
import com.skysam.hchirinos.diesan.common.dataClass.Lot
import com.skysam.hchirinos.diesan.common.dataClass.Sale
import com.skysam.hchirinos.diesan.databinding.FragmentSalesBinding
import com.skysam.hchirinos.diesan.ui.MainViewModel
import com.skysam.hchirinos.diesan.ui.common.WrapContentLinearLayoutManager
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator


class SalesFragment : Fragment(), SalesOnClick {
    private var _binding: FragmentSalesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var salesAdapter: SalesAdapter
    private val sales = mutableListOf<Sale>()
    private val lots = mutableListOf<Lot>()
    private lateinit var wrapContentLinearLayoutManager: WrapContentLinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSalesBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        wrapContentLinearLayoutManager = WrapContentLinearLayoutManager(requireContext(),
            RecyclerView.VERTICAL, false)
        salesAdapter = SalesAdapter(sales, this)
        binding.rvSales.apply {
            setHasFixedSize(true)
            adapter = salesAdapter
            layoutManager = wrapContentLinearLayoutManager
        }
        
        val itemTouchHelper = ItemTouchHelper(itemSwipe)
        itemTouchHelper.attachToRecyclerView(binding.rvSales)
        
        loadViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadViewModel() {
        viewModel.sales.observe(viewLifecycleOwner) {
            if (_binding != null) {
                if (it.isNotEmpty()) {
                    sales.clear()
                    sales.addAll(it)
                    salesAdapter.notifyItemRangeInserted(0, sales.size)
                    binding.rvSales.visibility = View.VISIBLE
                    binding.tvListEmpty.visibility = View.GONE
                } else {
                    binding.rvSales.visibility = View.GONE
                    binding.tvListEmpty.visibility = View.VISIBLE
                }
                binding.progressBar.visibility = View.GONE
            }
        }
        
        viewModel.lotsStock.observe(viewLifecycleOwner) {
            if (_binding != null) {
                lots.clear()
                lots.addAll(it)
            }
        }
    }
    
    override fun viewDetail(sale: Sale) {
        viewModel.viewSale(sale)
        val viewDetailsSaleDialog = ViewDetailsSaleDialog()
        viewDetailsSaleDialog.show(requireActivity().supportFragmentManager, tag)
    }
    
    private val itemSwipe = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }
    
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val sale = sales[viewHolder.adapterPosition]
            
            if (direction == ItemTouchHelper.RIGHT) {
                if (!sale.isAnulled) {
                    createDialogAnulled(sale)
                } else {
                    salesAdapter.notifyDataSetChanged()
                    Snackbar.make(binding.root, R.string.text_anull_already, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    
        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            RecyclerViewSwipeDecorator.Builder(
                c,
                recyclerView,
                viewHolder,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            )
                .addSwipeRightActionIcon(R.drawable.ic_money_off_24)
                .addSwipeRightBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.red_light
                    )
                )
                .create()
                .decorate()
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    
    }
    
    private fun createDialogAnulled(sale: Sale) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_confirmation_dialog))
            .setMessage(getString(R.string.msg_dialog_anull))
            .setPositiveButton(R.string.text_anull) { _, _ ->
                viewModel.anulledSale(sale)
                updateStock(sale)
            }
            .setNegativeButton(R.string.text_cancel) { _, _ ->
                salesAdapter.notifyDataSetChanged()
            }
    
        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.show()
    }
    
    private fun updateStock(sale: Sale) {
        val lot = lots.last()
        for (prodSale in sale.products) {
            var add = true
            for (prodLot in lot.products) {
                if (prodSale.name == prodLot.name && prodSale.priceByUnit == prodLot.priceByUnit
                    && prodSale.percentageProfit == prodLot.percentageProfit) {
                    lot.products[lot.products.indexOf(prodLot)].quantity = prodLot.quantity + prodSale.quantity
                    add = false
                    break
                }
            }
            if (add) lot.products.add(prodSale)
        }
        viewModel.updateStock(lot)
    }
}