package com.skysam.hchirinos.diesan.ui.stats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.core.util.Pair
import com.google.android.material.datepicker.MaterialDatePicker
import com.skysam.hchirinos.diesan.R
import com.skysam.hchirinos.diesan.common.Class
import com.skysam.hchirinos.diesan.databinding.FragmentStatsBinding
import java.util.*

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private lateinit var dateStart: Date
    private lateinit var dateFinal: Date
    private var monthByDefault = 0
    private var yearByDefault = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
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
        
        val calendar = Calendar.getInstance()
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        dateStart = calendar.time
        calendar[Calendar.HOUR_OF_DAY] = 23
        calendar[Calendar.MINUTE] = 59
        dateFinal = calendar.time
        monthByDefault = calendar[Calendar.MONTH]
        yearByDefault = calendar[Calendar.YEAR]
    
        binding.chipMonth.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                //isByRange = false
                configAdapter()
                binding.spinnerMonth.visibility = View.VISIBLE
                binding.spinnerYear.visibility = View.VISIBLE
                binding.tfDate.visibility = View.INVISIBLE
            }
        }
        binding.chipWeek.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                //isByRange = true
                binding.spinnerMonth.visibility = View.GONE
                binding.spinnerYear.visibility = View.GONE
                binding.tfDate.visibility = View.VISIBLE
                //loadData(true, -1, -1)
            }
        }
    
        binding.etDate.setText(getString(
            R.string.text_date_range,
            Class.convertDateToString(dateStart), Class.convertDateToString(dateFinal)))
        binding.etDate.setOnClickListener { selecDate() }
        binding.progressBar.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                getOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    
    private fun getOut() {
        requireActivity().finish()
    }
    
    private fun selecDate() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        val calendar = Calendar.getInstance()
        
        val picker = builder.build()
        picker.addOnPositiveButtonClickListener { selection: Pair<Long, Long> ->
            val timeZone = TimeZone.getDefault()
            val offset = timeZone.getOffset(Date().time) * -1
            calendar.timeInMillis = selection.first
            calendar.timeInMillis = calendar.timeInMillis + offset
            calendar[Calendar.HOUR_OF_DAY] = 0
            calendar[Calendar.MINUTE] = 0
            dateStart = calendar.time
            calendar.timeInMillis = selection.second
            calendar.timeInMillis = calendar.timeInMillis + offset
            calendar[Calendar.HOUR_OF_DAY] = 23
            calendar[Calendar.MINUTE] = 59
            dateFinal = calendar.time
            binding.etDate.setText(getString(R.string.text_date_range,
                Class.convertDateToString(dateStart), Class.convertDateToString(dateFinal)))
            //loadData(true, -1, -1)
        }
        picker.show(requireActivity().supportFragmentManager, picker.toString())
    }
    
    private fun configAdapter() {
        val selectionSpinner = monthByDefault
        val selectionSpinnerYear = if (yearByDefault == 2022) 0 else 1
        val listSpinnerMonth = listOf(*resources.getStringArray(R.array.months))
        val listSpinnerYear = listOf(*resources.getStringArray(R.array.years))
        
        val adapterUnits = ArrayAdapter(requireContext(), R.layout.layout_spinner, listSpinnerMonth)
        binding.spinnerMonth.apply {
            adapter = adapterUnits
            setSelection(selectionSpinner)
        }
        
        val adapterYear = ArrayAdapter(requireContext(), R.layout.layout_spinner, listSpinnerYear)
        binding.spinnerYear.apply {
            adapter = adapterYear
            setSelection(selectionSpinnerYear)
        }
    }
}