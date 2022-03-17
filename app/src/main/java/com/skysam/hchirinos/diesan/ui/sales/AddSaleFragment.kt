package com.skysam.hchirinos.diesan.ui.sales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.skysam.hchirinos.diesan.R
import com.skysam.hchirinos.diesan.common.Class
import com.skysam.hchirinos.diesan.common.Constants
import com.skysam.hchirinos.diesan.common.dataClass.Lot
import com.skysam.hchirinos.diesan.common.dataClass.Product
import com.skysam.hchirinos.diesan.common.dataClass.Sale
import com.skysam.hchirinos.diesan.databinding.FragmentAddSaleBinding
import com.skysam.hchirinos.diesan.ui.common.ExitDialog
import com.skysam.hchirinos.diesan.ui.common.OnClickExit
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.*

class AddSaleFragment: Fragment(), OnClickExit, AddSaleOnClick {
    private var _binding: FragmentAddSaleBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NewSaleViewModel by activityViewModels()
    private lateinit var adapterAddSale: AddSaleAdapter
    private val products = mutableListOf<Product>()
    private val lots = mutableListOf<Lot>()
    private val productsToSell = mutableListOf<Product>()
    private var dateSelected: Long = 0
    private lateinit var productToDelete: Product
    private var positionToEdit = -1

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
        binding.btnSale.setOnClickListener { validateData() }

        loadViewModel()
    }

    private fun loadViewModel() {
        viewModel.productsFromLots.observe(viewLifecycleOwner) {
            if (_binding != null) {
                products.clear()
                products.addAll(it)
                val listNamesProduct = mutableListOf<String>()
                for (prod in products) {
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
                if (productsToSell.size == it.size && positionToEdit >= 0) {
                    adapterAddSale.notifyItemChanged(positionToEdit)
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
        viewModel.lots.observe(viewLifecycleOwner) {
            if (_binding != null) {
                if (it.isNotEmpty()) {
                    lots.clear()
                    lots.addAll(it)
                    lots.sortBy { lot -> lot.date }
                }
            }
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
        requireActivity().finish()
    }
    
    override fun check(product: Product, isCheck: Boolean) {
        val newPriceToSell = if (isCheck) product.priceToSell - 1 else product.priceToSell + 1
        val productEdited = Product(
            product.id,
            product.name,
            product.price,
            product.quantity,
            product.ship,
            product.tax,
            product.sumTotal,
            product.priceByUnit,
            product.percentageProfit,
            newPriceToSell,
            product.amountProfit,
            product.image,
            isCheck
        )
        positionToEdit = productsToSell.indexOf(product)
        viewModel.editProductToSell(productEdited)
    }
    
    override fun delete(product: Product) {
        productToDelete = product
        viewModel.removeProducToSell(product)
    }

    override fun edit(product: Product) {
        var quantityAvailable = 1
        val arrayQuantities = mutableListOf<String>()
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
                    product.image,
                    product.isCheck
                )
                positionToEdit = productsToSell.indexOf(product)
                viewModel.editProductToSell(productEdited)
                dialogInterface.dismiss()
            }

        val dialog = builder.create()
        dialog.setCancelable(true)
        dialog.show()
    }

    private fun validateData() {
        if (productsToSell.isEmpty()) {
            Snackbar.make(binding.root, getString(R.string.error_list_empty), Snackbar.LENGTH_LONG).show()
            binding.etProduct.requestFocus()
            return
        }
        val customer = binding.etCustomer.text.toString().ifEmpty { "" }
        val sale = Sale(
            Constants.ID,
            Date(dateSelected),
            customer,
            productsToSell
        )
        viewModel.saveSale(sale)
    
        val lotsFinal = mutableListOf<Lot>()
        lotsFinal.addAll(lots)
        val quantitiesProducts = mutableListOf<Product>()
        quantitiesProducts.addAll(productsToSell)
        for (lot in lotsFinal) {
            val productsByLot = mutableListOf<Product>()
            productsByLot.addAll(lot.products)
            for (pr in productsByLot) {
                for (pro in quantitiesProducts) {
                    if (pro.name == pr.name && pro.priceByUnit == pr.priceByUnit
                        && pro.percentageProfit == pr.percentageProfit) {
                        val quanityFinal = pr.quantity - pro.quantity
                        if (quanityFinal == 0) {
                            lot.products.remove(pr)
                            pro.quantity = 0
                        }
                        if (quanityFinal < 0) {
                            lot.products.remove(pr)
                            pro.quantity = quanityFinal * (-1)
                        }
                        if (quanityFinal > 0) {
                            lot.products[lot.products.indexOf(pr)].quantity = quanityFinal
                            pro.quantity = 0
                        }
                    }
                }
            }
            viewModel.updateStock(lot)
        }
    
        binding.progressBar.visibility = View.VISIBLE
        Snackbar.make(binding.root, getString(R.string.text_generate_new_sale), Snackbar.LENGTH_INDEFINITE).show()
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        lifecycleScope.launch {
            delay(2000)
            requireActivity().finish()
        }
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