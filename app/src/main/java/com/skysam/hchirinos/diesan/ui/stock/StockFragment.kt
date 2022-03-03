package com.skysam.hchirinos.diesan.ui.stock

import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.skysam.hchirinos.diesan.R
import com.skysam.hchirinos.diesan.common.dataClass.Lot
import com.skysam.hchirinos.diesan.common.dataClass.Product
import com.skysam.hchirinos.diesan.databinding.FragmentStockBinding


class StockFragment : Fragment(), StockOnClick {
    private var _binding: FragmentStockBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StockViewModel by activityViewModels()
    private lateinit var adapterItems: ItemDetailsStockAdapter
    private val products = mutableListOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStockBinding.inflate(inflater, container, false)
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
        adapterItems = ItemDetailsStockAdapter(products)
        binding.rvStock.apply {
            setHasFixedSize(true)
            adapter = adapterItems
        }
        loadViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
    }
    
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.stock, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                getOut()
                true
            }
            R.id.action_list_stock -> {
                findNavController().navigate(R.id.action_FragmentStock_to_listLotsFragment)
                true
            }
            R.id.action_share -> {
                share()
                true
            }
            R.id.action_add_sale -> {
                findNavController().navigate(R.id.action_FragmentStock_to_addSaleFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun loadViewModel() {
        viewModel.lots.observe(viewLifecycleOwner) {
            if (_binding != null) {
                products.clear()
                for (lot in it) {
                    products.addAll(lot.products)
                    products.sortBy { product -> product.name }
                    /*if (products.isNotEmpty()) {
                        for (prod in lot.products) {
                            for (produc in products) {
                                if (produc.name == prod.name) {
                                    produc.quantity = produc.quantity + prod.quantity
                                } else {
                                    products.add(prod)
                                }
                            }
                        }
                    } else {
                        products.addAll(lot.products)
                    }*/
                }
                if (products.isEmpty()) {
                    binding.rvStock.visibility = View.GONE
                    binding.tvListEmpty.visibility = View.VISIBLE
                } else {
                    binding.rvStock.visibility = View.VISIBLE
                    binding.tvListEmpty.visibility = View.GONE
                }
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun getOut() {
        requireActivity().finish()
    }

    override fun viewDetail(lot: Lot) {
        viewModel.viewLot(lot)
        val viewDetailsStockDialog = ViewDetailsStockDialog()
        viewDetailsStockDialog.show(requireActivity().supportFragmentManager, tag)
    }

    private fun share() {
       /* val selection = StringBuilder()
        for (item in lots) {
            selection.append("\n").append("${item.name}: $${Class.convertDoubleToString(item.priceToSell)}")
        }
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, selection.toString())
        startActivity(Intent.createChooser(intent, getString(R.string.title_share_dialog)))*/
    }
}