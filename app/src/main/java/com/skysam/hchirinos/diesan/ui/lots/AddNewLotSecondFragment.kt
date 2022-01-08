package com.skysam.hchirinos.diesan.ui.lots

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.skysam.hchirinos.diesan.R
import com.skysam.hchirinos.diesan.common.dataClass.Product
import com.skysam.hchirinos.diesan.databinding.FragmentAddNewLotSecondBinding
import java.text.DateFormat
import java.util.*

class AddNewLotSecondFragment : Fragment() {

    private var _binding: FragmentAddNewLotSecondBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LotsViewModel by activityViewModels()
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

        adapterItems = ItemsDetailsNewLotAdapter(products)
        binding.rvProducts.apply {
            setHasFixedSize(true)
            adapter = adapterItems
        }

        binding.etDate.setOnClickListener { selecDate() }
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