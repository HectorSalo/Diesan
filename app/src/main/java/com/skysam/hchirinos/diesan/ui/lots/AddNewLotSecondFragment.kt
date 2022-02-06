package com.skysam.hchirinos.diesan.ui.lots

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.skysam.hchirinos.diesan.R
import com.skysam.hchirinos.diesan.common.Class
import com.skysam.hchirinos.diesan.common.dataClass.Product
import com.skysam.hchirinos.diesan.databinding.FragmentAddNewLotSecondBinding
import java.text.DateFormat
import java.util.*

class AddNewLotSecondFragment : Fragment(), TextWatcher {

    private var _binding: FragmentAddNewLotSecondBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NewLotViewModel by activityViewModels()
    private val products = mutableListOf<Product>()
    private lateinit var adapterItems: ItemsDetailsNewLotAdapter
    private var dateSelected: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddNewLotSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dateSelected = Date().time
        binding.etDate.setText(DateFormat.getDateInstance().format(Date()))
        binding.etShip.addTextChangedListener(this)

        binding.etNumberLot.inputType = InputType.TYPE_CLASS_NUMBER
        binding.etShip.inputType = InputType.TYPE_CLASS_NUMBER

        adapterItems = ItemsDetailsNewLotAdapter(products)
        binding.rvProducts.apply {
            setHasFixedSize(true)
            adapter = adapterItems
        }

        binding.etDate.setOnClickListener { selecDate() }
        binding.btnSave.setOnClickListener { validateData() }
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

        loadViewModels()
    }

    private fun loadViewModels() {
        viewModel.products.observe(viewLifecycleOwner, {
            if (_binding != null) {
                products.clear()
                products.addAll(it)
            }
        })
        viewModel.ship.observe(viewLifecycleOwner, {
            if (_binding != null) {
                binding.etShip.removeTextChangedListener(this)
                binding.etShip.setText(Class.convertDoubleToString(it))
                binding.etShip.setSelection(Class.convertDoubleToString(it).length)
                binding.etShip.addTextChangedListener(this)

                val costShipByItem = it / products.size
                binding.etItemShip.setText(Class.convertDoubleToString(costShipByItem))

                for (item in products) {
                    item.sumTotal = (item.price + item.tax + it) * item.quantity
                    item.priceByUnit = item.sumTotal / item.quantity
                    item.priceToSell = (item.priceByUnit + item.percentageProfit) + item.priceByUnit
                    item.amountProfit = item.priceToSell - item.priceByUnit
                }
                adapterItems.notifyItemRangeChanged(0, products.size)
            }
        })
    }

    private fun validateData() {
        binding.tfNumberLot.error = null
        binding.tfShip.error = null

        val numberLot = binding.etNumberLot.text.toString()
        if (numberLot.isEmpty()) {
            Snackbar.make(binding.root, getString(R.string.error_field_empty), Snackbar.LENGTH_LONG).show()
            binding.etNumberLot.requestFocus()
            return
        }
        val numberLotInt = numberLot.toInt()
        if (numberLotInt == 0) {
            binding.tfNumberLot.error = getString(R.string.error_price_zero)
            binding.etNumberLot.requestFocus()
            return
        }
        var ship = binding.etShip.text.toString()
        if (ship == "0,00") {
            binding.tfShip.error = getString(R.string.error_price_zero)
            binding.etShip.requestFocus()
            return
        }
        ship = ship.replace(".", "").replace(",", ".")

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun afterTextChanged(s: Editable?) {
        var cadena = s.toString()
        cadena = cadena.replace(",", "").replace(".", "")
        val cantidad: Double = cadena.toDouble() / 100

        if (s.toString() == binding.etShip.text.toString()) {
            viewModel.valueShip(cantidad)
        }
    }
}