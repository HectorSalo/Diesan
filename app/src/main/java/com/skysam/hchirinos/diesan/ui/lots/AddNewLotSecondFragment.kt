package com.skysam.hchirinos.diesan.ui.lots

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.skysam.hchirinos.diesan.R
import com.skysam.hchirinos.diesan.common.Class
import com.skysam.hchirinos.diesan.common.Constants
import com.skysam.hchirinos.diesan.common.dataClass.Lot
import com.skysam.hchirinos.diesan.common.dataClass.Product
import com.skysam.hchirinos.diesan.databinding.FragmentAddNewLotSecondBinding
import java.text.DateFormat
import java.util.*

class AddNewLotSecondFragment : Fragment() {

    private var _binding: FragmentAddNewLotSecondBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NewLotViewModel by activityViewModels()
    private val products = mutableListOf<Product>()
    private val productsOlder = mutableListOf<Product>()
    private val lots = mutableListOf<Lot>()
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

        binding.etNumberLot.inputType = InputType.TYPE_CLASS_NUMBER

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
        viewModel.lots.observe(viewLifecycleOwner) {
            if (_binding != null) {
                if (it.isNotEmpty()) {
                    lots.clear()
                    lots.addAll(it)
                    val nextNumber = it[0].numberLot + 1
                    binding.etNumberLot.setText(nextNumber.toString())
                } else {
                    binding.etNumberLot.setText("1")
                }
            }
        }
        viewModel.productsOlder.observe(viewLifecycleOwner) {
            if (_binding != null) {
                productsOlder.clear()
                productsOlder.addAll(it)
            }
        }
        viewModel.products.observe(viewLifecycleOwner) {
            if (_binding != null) {
                products.clear()
                products.addAll(it)
                adapterItems.notifyItemRangeInserted(0, products.size)
            }
        }
    }

    private fun validateData() {
        binding.tfNumberLot.error = null

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
        for (lot in lots) {
            if (lot.numberLot == numberLotInt) {
                binding.tfNumberLot.error = getString(R.string.error_number_exists)
                binding.etNumberLot.requestFocus()
                return
            }
        }
       
        Class.keyboardClose(binding.root)
        val newLot = Lot(
            Constants.ID,
            numberLotInt,
            Date(dateSelected),
            0.0,
            products
        )
        viewModel.sendNewLot(newLot, productsOlder)
        Toast.makeText(requireContext(), getString(R.string.text_succes), Toast.LENGTH_SHORT).show()
        requireActivity().finish()
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
}