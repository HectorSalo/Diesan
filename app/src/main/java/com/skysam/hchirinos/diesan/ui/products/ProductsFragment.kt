package com.skysam.hchirinos.diesan.ui.products

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.skysam.hchirinos.diesan.R
import com.skysam.hchirinos.diesan.common.dataClass.Product
import com.skysam.hchirinos.diesan.databinding.FragmentProductsBinding
import com.skysam.hchirinos.diesan.ui.settings.SettingsActivity

class ProductsFragment : Fragment(), SearchView.OnQueryTextListener {

    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapterProduct: ProductAdapter
    private var products: MutableList<Product> = mutableListOf()
    private lateinit var search: SearchView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapterProduct = ProductAdapter(products)
        binding.rvProducts.apply {
            setHasFixedSize(true)
            adapter = adapterProduct
        }
        binding.fab.setOnClickListener {
            val addProductDialog = AddProductDialog(products)
            addProductDialog.show(requireActivity().supportFragmentManager, tag)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.main, menu)
        val itemSearch = menu.findItem(R.id.action_search)
        search = itemSearch.actionView as SearchView
        search.setOnQueryTextListener(this)
        val itemSettings = menu.findItem(R.id.action_settings)
        itemSettings.setOnMenuItemClickListener {
            requireActivity().startActivity(Intent(requireContext(), SettingsActivity::class.java))
            true
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }
}