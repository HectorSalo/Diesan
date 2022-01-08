package com.skysam.hchirinos.diesan.ui.lots

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.skysam.hchirinos.diesan.R
import com.skysam.hchirinos.diesan.common.Class
import com.skysam.hchirinos.diesan.common.Constants
import com.skysam.hchirinos.diesan.common.dataClass.Product
import com.skysam.hchirinos.diesan.databinding.FragmentAddNewLotFirstBinding
import com.skysam.hchirinos.diesan.ui.common.DecimalInputFilter
import com.skysam.hchirinos.diesan.ui.common.ExitDialog
import com.skysam.hchirinos.diesan.ui.common.OnClickExit
import java.util.*


class AddNewLotFirstFragment : Fragment(), OnClickInterface, OnClickExit, TextWatcher {

    private var _binding: FragmentAddNewLotFirstBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LotsViewModel by activityViewModels()
    private lateinit var adapterNewLot: ItemsNewLotAdapter
    private val products = mutableListOf<Product>()
    private lateinit var productToDelete: Product

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
        binding.etProfit.filters =
            arrayOf<InputFilter>(DecimalInputFilter(3,2))
        binding.etPrice.addTextChangedListener(this)
        binding.etTax.addTextChangedListener(this)

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
        viewModel.products.observe(viewLifecycleOwner, {
            if (_binding != null) {
                if (it.isNotEmpty()) {
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
                        binding.etPrice.setText(getString(R.string.text_price_init))
                        binding.etQuantity.setText("")
                        binding.etProfit.setText("")
                        binding.etTax.setText(getString(R.string.text_price_init))
                        binding.etProduct.requestFocus()
                    }
                    binding.tvListEmpty.visibility = View.GONE
                    binding.rvProducts.visibility = View.VISIBLE
                } else {
                    binding.tvListEmpty.visibility = View.VISIBLE
                    binding.rvProducts.visibility = View.INVISIBLE
                }
            }
        })
        viewModel.total.observe(viewLifecycleOwner, {
            binding.tvTotal.text = getString(R.string.text_total_dolar,
                Class.convertDoubleToString(it))
        })
    }

    private fun validateData() {
        binding.tfProduct.error = null
        binding.tfPrice.error = null
        binding.tfQuantity.error = null

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
        val profit = binding.etProfit.text.toString()
        if (profit.isEmpty()) {
            binding.tfProfit.error = getString(R.string.error_field_empty)
            binding.etProfit.requestFocus()
            return
        }
        val profitD = profit.toDouble()
        if (profitD <= 0) {
            binding.tfProfit.error = getString(R.string.error_price_zero)
            binding.etProfit.requestFocus()
            return
        }
        val product = Product(
            Constants.ID,
            name,
            price.toDouble(),
            quantityInt,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0
        )
        viewModel.addProduct(product)
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
    }
}