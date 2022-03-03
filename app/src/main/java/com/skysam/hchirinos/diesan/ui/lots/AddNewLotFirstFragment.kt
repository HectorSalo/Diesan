package com.skysam.hchirinos.diesan.ui.lots

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.skysam.hchirinos.diesan.R
import com.skysam.hchirinos.diesan.common.Class
import com.skysam.hchirinos.diesan.common.dataClass.Product
import com.skysam.hchirinos.diesan.databinding.FragmentAddNewLotFirstBinding
import com.skysam.hchirinos.diesan.ui.common.ExitDialog
import com.skysam.hchirinos.diesan.ui.common.OnClickExit
import java.util.*


class AddNewLotFirstFragment : Fragment(), OnClickInterface, OnClickExit, TextWatcher {

    private var _binding: FragmentAddNewLotFirstBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NewLotViewModel by activityViewModels()
    private lateinit var adapterNewLot: ItemsNewLotAdapter
    private val products = mutableListOf<Product>()
    private val productsOlder = mutableListOf<Product>()
    private lateinit var productToDelete: Product
    private var productSelected: Product? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddNewLotFirstBinding.inflate(inflater, container, false)
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

        binding.etQuantity.inputType = InputType.TYPE_CLASS_NUMBER
        binding.etPrice.inputType = InputType.TYPE_CLASS_NUMBER
        binding.etProfit.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        binding.etItemShip.inputType = InputType.TYPE_CLASS_NUMBER
        binding.etProfit.addTextChangedListener(this)
        binding.etPrice.addTextChangedListener(this)
        binding.etTax.addTextChangedListener(this)
        binding.etItemShip.addTextChangedListener(this)

        binding.etProduct.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            Class.keyboardClose(binding.root)
            val nameSelected = parent.getItemAtPosition(position)
            for (pro in productsOlder) {
                if (pro.name == nameSelected) productSelected = pro
            }
            binding.etPrice.setText(Class.convertDoubleToString(productSelected!!.price))
            binding.etQuantity.setText(productSelected?.quantity.toString())
            binding.etTax.setText(Class.convertDoubleToString(productSelected!!.tax))
            binding.etProfit.setText(Class.convertDoubleToString(productSelected!!.percentageProfit))
            binding.etItemShip.setText(Class.convertDoubleToString(productSelected!!.ship))
        }

        adapterNewLot = ItemsNewLotAdapter(products, this)
        binding.rvProducts.apply {
            setHasFixedSize(true)
            adapter = adapterNewLot
        }
        binding.fabAddProduct.setOnClickListener {
            validateData()
        }
        binding.btnTotal.setOnClickListener {
            if (products.isEmpty()) {
                Snackbar.make(binding.root, getString(R.string.error_list_empty), Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        binding.btnExit.setOnClickListener { getOut() }
        loadViewModel()
    }

    private fun loadViewModel() {
        viewModel.productsOlder.observe(viewLifecycleOwner) {
            if (_binding != null) {
                if (it.isNotEmpty()) {
                    productsOlder.clear()
                    productsOlder.addAll(it)
                    val listNamesProduct = mutableListOf<String>()
                    for (prod in it) {
                        listNamesProduct.add(prod.name)
                    }
                    val adapterAutocomplete = ArrayAdapter(requireContext(),
                        R.layout.layout_list_autocomplete, listNamesProduct.sorted())
                    binding.etProduct.setAdapter(adapterAutocomplete)
                }
            }
        }
        viewModel.products.observe(viewLifecycleOwner) {
            if (_binding != null) {
                if (products.size > it.size) {
                    adapterNewLot.notifyItemRemoved(products.indexOf(productToDelete))
                    products.clear()
                    products.addAll(it)
                }
                if (products.size < it.size) {
                    products.clear()
                    products.addAll(it)
                    adapterNewLot.notifyItemInserted(products.size - 1)
                    binding.etProduct.setText("")
                    binding.etItemShip.setText(getString(R.string.text_price_init))
                    binding.etPrice.setText(getString(R.string.text_price_init))
                    binding.etQuantity.setText("")
                    binding.etProfit.setText("")
                    binding.etTax.setText(getString(R.string.text_price_init))
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

    private fun validateData() {
        binding.tfProduct.error = null
        binding.tfPrice.error = null
        binding.tfQuantity.error = null
        binding.tfProfit.error = null
        binding.tfItemShip.error = null

        val name = binding.etProduct.text.toString().trim()
        if (name.isEmpty()) {
            binding.tfProduct.error = getString(R.string.error_field_empty)
            binding.etProduct.requestFocus()
            return
        }
        var price = binding.etPrice.text.toString()
        if (price == "0,00") {
            binding.tfPrice.error = getString(R.string.error_price_zero)
            binding.etPrice.requestFocus()
            return
        }
        price = price.replace(".", "").replace(",", ".")
        var ship = binding.etItemShip.text.toString()
        if (ship == "0,00") {
            binding.tfItemShip.error = getString(R.string.error_price_zero)
            binding.etItemShip.requestFocus()
            return
        }
        ship = ship.replace(".", "").replace(",", ".")
        val quantity = binding.etQuantity.text.toString()
        if (quantity.isEmpty()) {
            binding.tfQuantity.error = getString(R.string.error_field_empty)
            binding.etQuantity.requestFocus()
            return
        }
        val quantityInt = quantity.toInt()
        if (quantityInt == 0) {
            binding.tfQuantity.error = getString(R.string.error_price_zero)
            binding.etQuantity.requestFocus()
            return
        }
        var profitPercentage = binding.etProfit.text.toString()
        profitPercentage = profitPercentage.replace(".", "").replace(",", ".")
        if (profitPercentage.isEmpty()) {
            binding.tfProfit.error = getString(R.string.error_field_empty)
            binding.etProfit.requestFocus()
            return
        }
        val profitPerD = profitPercentage.toDouble() / 100.00
        if (profitPerD <= 0) {
            binding.tfProfit.error = getString(R.string.error_price_zero)
            binding.etProfit.requestFocus()
            return
        }
        var tax = binding.etTax.text.toString()
        tax = tax.replace(".", "").replace(",", ".")

        val productToSend: Product
        if (productSelected != null) {
            if (productSelected!!.name == name) {
                productSelected!!.price = price.toDouble()
                productSelected!!.ship = ship.toDouble()
                productSelected!!.quantity = quantityInt
                productSelected!!.tax = tax.toDouble()
                productSelected!!.percentageProfit = profitPerD
                productToSend = productSelected!!
            } else {
                productToSend = Product(
                    Date().time.toString(),
                    name,
                    price.toDouble(),
                    quantityInt,
                    ship.toDouble(),
                    tax.toDouble(),
                    0.0,
                    0.0,
                    profitPerD,
                    0.0,
                    0.0,
                    ""
                )
            }
        } else {
            productToSend = Product(
                Date().time.toString(),
                name,
                price.toDouble(),
                quantityInt,
                ship.toDouble(),
                tax.toDouble(),
                0.0,
                0.0,
                profitPerD,
                0.0,
                0.0,
                ""
            )
        }
        productToSend.sumTotal = (productToSend.price + productToSend.tax + productToSend.ship) *
                productToSend.quantity
        productToSend.priceByUnit = productToSend.sumTotal / productToSend.quantity
        productToSend.priceToSell = (productToSend.priceByUnit * productToSend.percentageProfit) +
                productToSend.priceByUnit
        productToSend.amountProfit = productToSend.priceToSell - productToSend.priceByUnit
        viewModel.addProduct(productToSend)
        viewModel.addTotal(price.toDouble() * quantityInt)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun delete(product: Product) {
        productToDelete = product
        viewModel.restTotal(product.price * product.quantity)
        viewModel.removeProduct(product)
    }

    private fun getOut() {
        val exitDialog = ExitDialog(this)
        exitDialog.show(requireActivity().supportFragmentManager, tag)
    }

    override fun onClickExit() {
        requireActivity().finish()
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
            }
            if (s.toString() == binding.etTax.text.toString()) {
                binding.etTax.removeTextChangedListener(this)
                binding.etTax.setText(cadena)
                binding.etTax.setSelection(cadena.length)
                binding.etTax.addTextChangedListener(this)
            }
            if (s.toString() == binding.etProfit.text.toString()) {
                binding.etProfit.removeTextChangedListener(this)
                binding.etProfit.setText(cadena)
                binding.etProfit.setSelection(cadena.length)
                binding.etProfit.addTextChangedListener(this)
            }
            if (s.toString() == binding.etItemShip.text.toString()) {
                binding.etItemShip.removeTextChangedListener(this)
                binding.etItemShip.setText(cadena)
                binding.etItemShip.setSelection(cadena.length)
                binding.etItemShip.addTextChangedListener(this)
            }
        }
    }
}