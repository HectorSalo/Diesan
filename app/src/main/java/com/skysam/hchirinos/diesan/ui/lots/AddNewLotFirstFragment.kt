package com.skysam.hchirinos.diesan.ui.lots

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.skysam.hchirinos.diesan.R
import com.skysam.hchirinos.diesan.common.Class
import com.skysam.hchirinos.diesan.common.Constants
import com.skysam.hchirinos.diesan.common.dataClass.Product
import com.skysam.hchirinos.diesan.databinding.FragmentAddNewLotFirstBinding
import com.skysam.hchirinos.diesan.ui.common.ExitDialog
import com.skysam.hchirinos.diesan.ui.common.OnClickExit
import java.util.*


class AddNewLotFirstFragment : Fragment(), OnClickInterface, OnClickExit {

    private var _binding: FragmentAddNewLotFirstBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LotsViewModel by activityViewModels()
    private lateinit var adapterNewLot: ItemsNewLotAdapter
    private val products = mutableListOf<Product>()

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
        binding.etPrice.addTextChangedListener(object : TextWatcher{
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
            }

        })
        adapterNewLot = ItemsNewLotAdapter(products, this)
        binding.rvProducts.apply {
            setHasFixedSize(true)
            adapter = adapterNewLot
        }
        binding.fabAddProduct.setOnClickListener {
            validateData()
        }
        binding.btnTotal.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        binding.btnExit.setOnClickListener { getOut() }
        loadViewModel()
    }

    private fun loadViewModel() {
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
        val product = Product(
            Constants.ID,
            name,
            price.toDouble(),
            quantityInt
        )
        products.add(product)
        adapterNewLot.notifyItemInserted(products.size - 1)
        viewModel.addTotal(price.toDouble() * quantityInt)

        binding.etProduct.setText("")
        binding.etPrice.setText(getString(R.string.text_price_init))
        binding.etQuantity.setText("")
        binding.etProduct.requestFocus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun delete(product: Product) {
        viewModel.restTotal(product.price * product.quantity)
        adapterNewLot.notifyItemRemoved(products.indexOf(product))
        products.remove(product)
    }

    private fun getOut() {
        val exitDialog = ExitDialog(this)
        exitDialog.show(requireActivity().supportFragmentManager, tag)
    }

    override fun onClickExit() {
        requireActivity().finish()
    }
}