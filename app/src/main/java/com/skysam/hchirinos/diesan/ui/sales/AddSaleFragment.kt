package com.skysam.hchirinos.diesan.ui.sales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.skysam.hchirinos.diesan.R
import com.skysam.hchirinos.diesan.common.Class
import com.skysam.hchirinos.diesan.common.dataClass.Lot
import com.skysam.hchirinos.diesan.common.dataClass.Product
import com.skysam.hchirinos.diesan.databinding.FragmentAddSaleBinding
import com.skysam.hchirinos.diesan.ui.common.ExitDialog
import com.skysam.hchirinos.diesan.ui.common.OnClickExit
import com.skysam.hchirinos.diesan.ui.stock.StockViewModel
import java.text.DateFormat
import java.util.*

class AddSaleFragment: Fragment(), OnClickExit, AddSaleOnClick {
    private var _binding: FragmentAddSaleBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StockViewModel by activityViewModels()
    private lateinit var adapterAddSale: AddSaleAdapter
    private val products = mutableListOf<Product>()
    private val productsToSell = mutableListOf<Product>()
    private lateinit var lotToSell: Lot
    private var dateSelected: Long = 0
    private lateinit var productToDelete: Product

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddSaleBinding.inflate(inflater, container, false)
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

        dateSelected = Date().time
        binding.etDate.setText(DateFormat.getDateInstance().format(Date()))
        adapterAddSale = AddSaleAdapter(productsToSell, this)
        binding.rvProducts.apply {
            setHasFixedSize(true)
            adapter = adapterAddSale
        }

        binding.etProduct.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            Class.keyboardClose(binding.root)
            val nameSelected = parent.getItemAtPosition(position)
            var productSelected: Product? = null
            for (pro in products) {
                if (pro.name == nameSelected) {
                    productSelected = Product(
                        pro.id,
                        pro.name,
                        pro.price,
                        1,
                        pro.ship,
                        pro.tax,
                        pro.sumTotal,
                        pro.priceByUnit,
                        pro.percentageProfit,
                        pro.priceToSell,
                        pro.amountProfit,
                        pro.image)
                }
            }
            viewModel.addProducToSell(productSelected!!)
        }

        binding.etDate.setOnClickListener { selecDate() }
        binding.btnExit.setOnClickListener { getOut() }

        loadViewModel()
    }

    private fun loadViewModel() {
        viewModel.lotToSell.observe(viewLifecycleOwner) {
            if (_binding != null) {
                lotToSell = it
                products.clear()
                products.addAll(it.products)
                binding.tvTitle.text = getString(R.string.text_number_lot_item,
                    it.numberLot.toString())
                val listNamesProduct = mutableListOf<String>()
                for (prod in it.products) {
                    listNamesProduct.add(prod.name)
                }
                val adapterAutocomplete = ArrayAdapter(requireContext(),
                    R.layout.layout_list_autocomplete, listNamesProduct.sorted())
                binding.etProduct.setAdapter(adapterAutocomplete)
            }
        }
        viewModel.productsToSell.observe(viewLifecycleOwner) {
            if (_binding != null) {
                if (productsToSell.size > it.size) {
                    adapterAddSale.notifyItemRemoved(productsToSell.indexOf(productToDelete))
                    productsToSell.clear()
                    productsToSell.addAll(it)
                }
                if (productsToSell.size < it.size) {
                    productsToSell.clear()
                    productsToSell.addAll(it)
                    adapterAddSale.notifyItemInserted(productsToSell.size - 1)
                    binding.etProduct.setText("")
                    binding.etProduct.requestFocus()
                }
                if (it.isNotEmpty()) {
                    binding.tvListEmpty.visibility = View.GONE
                    binding.rvProducts.visibility = View.VISIBLE
                } else {
                    binding.tvListEmpty.visibility = View.VISIBLE
                    binding.rvProducts.visibility = View.INVISIBLE
                }
            }
        }
        viewModel.total.observe(viewLifecycleOwner) {
            binding.tvTotal.text = getString(
                R.string.text_total_dolar,
                Class.convertDoubleToString(it)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
    }

    private fun getOut() {
        val exitDialog = ExitDialog(this)
        exitDialog.show(requireActivity().supportFragmentManager, tag)
    }

    override fun onClickExit() {
        findNavController().navigate(R.id.action_addSaleFragment_to_FragmentStock)
    }

    override fun delete(product: Product) {
        productToDelete = product
        viewModel.removeProducToSell(product)
    }

    override fun edit(product: Product) {
        var quantityAvailable = 1
        val arrayQuantities = mutableListOf<String>()
        val test = mutableListOf<Product>()
        test.addAll(products)
        for (pro in products) {
            if (pro.id == product.id) {
                quantityAvailable = pro.quantity
            }
        }
        for (i in 1..quantityAvailable) {
            arrayQuantities.add(i.toString())
        }
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_select_quantity_dialog))
            .setSingleChoiceItems(arrayQuantities.toTypedArray(), product.quantity - 1)
            { dialogInterface, i ->
                val productEdited = Product(
                    product.id,
                    product.name,
                    product.price,
                    arrayQuantities[i].toInt(),
                    product.ship,
                    product.tax,
                    product.sumTotal,
                    product.priceByUnit,
                    product.percentageProfit,
                    product.priceToSell,
                    product.amountProfit,
                    product.image)
                adapterAddSale.notifyItemChanged(productsToSell.indexOf(product))
                viewModel.editProductToSell(productEdited)
                dialogInterface.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }

    private fun selecDate() {
        val builder = MaterialDatePicker.Builder.datePicker()
        val calendar = Calendar.getInstance()
        val picker = builder.build()
        picker.addOnPositiveButtonClickListener { selection: Long? ->
            calendar.timeInMillis = selection!!
            val timeZone = TimeZone.getDefault()
            val offset = timeZone.getOffset(Date().time) * -1
            calendar.timeInMillis = calendar.timeInMillis + offset
            val dateSelec = calendar.time
            dateSelected = dateSelec.time
            binding.etDate.setText(DateFormat.getDateInstance().format(dateSelected))
        }
        picker.show(requireActivity().supportFragmentManager, picker.toString())
    }
}