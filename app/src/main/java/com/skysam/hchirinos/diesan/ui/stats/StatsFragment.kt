package com.skysam.hchirinos.diesan.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.skysam.hchirinos.diesan.R
import com.skysam.hchirinos.diesan.common.Class
import com.skysam.hchirinos.diesan.common.dataClass.Sale
import com.skysam.hchirinos.diesan.databinding.FragmentStatsBinding
import java.util.*

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StatsViewModel by activityViewModels()
    private val sales = mutableListOf<Sale>()
    private lateinit var dateStart: Date
    private lateinit var dateFinal: Date
    private var monthSelected = 0
    private var yearSelected = 0
    private var isByRange = true

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
        monthSelected = calendar[Calendar.MONTH]
        yearSelected = calendar[Calendar.YEAR]
    
        binding.chipMonth.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                isByRange = false
                configAdapter()
                binding.spinnerMonth.visibility = View.VISIBLE
                binding.spinnerYear.visibility = View.VISIBLE
                binding.tfDate.visibility = View.INVISIBLE
            }
        }
        binding.chipWeek.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                isByRange = true
                binding.spinnerMonth.visibility = View.GONE
                binding.spinnerYear.visibility = View.GONE
                binding.tfDate.visibility = View.VISIBLE
                loadData()
            }
        }
    
        binding.etDate.setText(getString(
            R.string.text_date_range,
            Class.convertDateToString(dateStart), Class.convertDateToString(dateFinal)))
        binding.etDate.setOnClickListener { selecDate() }
    
        binding.spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                monthSelected = position
                loadData()
            }
        
            override fun onNothingSelected(parent: AdapterView<*>?) {
            
            }
        }
        binding.spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                yearSelected = position + 2022
                loadData()
            }
        
            override fun onNothingSelected(p0: AdapterView<*>?) {
            
            }
        
        }
        
        loadViewModel()
    }
    
    private fun loadViewModel() {
        viewModel.sales.observe(viewLifecycleOwner) {
            if (_binding != null) {
                sales.clear()
                for (sal in it) {
                    if (!sal.isAnulled) sales.add(sal)
                }
                loadData()
            }
        }
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
    
    private fun loadData() {
        var totalSales = 0.0
        var totalProfit = 0.0
        var totalDelivery = 0.0
        
        for (sale in sales) {
            if (isByRange) {
                if (sale.date.after(dateStart) && sale.date.before(dateFinal)) {
                    for (pro in sale.products) {
                        totalSales += pro.quantity * pro.priceToSell
                        totalProfit += pro.quantity * pro.amountProfit
                    }
                    totalDelivery += sale.delivery
                }
            } else {
                val calendar = Calendar.getInstance()
                calendar.time = sale.date
                if (calendar[Calendar.MONTH] == monthSelected && calendar[Calendar.YEAR] == yearSelected) {
                    for (pro in sale.products) {
                        totalSales += pro.quantity * pro.priceToSell
                        totalProfit += pro.quantity * pro.amountProfit
                    }
                    totalDelivery += sale.delivery
                }
            }
        }
        
        binding.tvSale.text = getString(R.string.text_amount_add_graph,
            Class.convertDoubleToString(totalSales))
        binding.tvProfit.text = getString(R.string.text_amount_add_graph,
            Class.convertDoubleToString(totalProfit))
        binding.tvDelivery.text = Class.convertDoubleToString(totalDelivery)
        binding.progressBar.visibility = View.GONE
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
            loadData()
        }
        picker.show(requireActivity().supportFragmentManager, picker.toString())
    }
    
    private fun configAdapter() {
        val selectionSpinner = monthSelected
        val selectionSpinnerYear = yearSelected - 2022
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