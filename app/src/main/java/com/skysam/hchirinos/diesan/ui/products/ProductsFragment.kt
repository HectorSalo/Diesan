package com.skysam.hchirinos.diesan.ui.products

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.skysam.hchirinos.diesan.R
import com.skysam.hchirinos.diesan.common.dataClass.Product
import com.skysam.hchirinos.diesan.common.dataClass.ProductsToDelete
import com.skysam.hchirinos.diesan.databinding.FragmentProductsBinding
import com.skysam.hchirinos.diesan.ui.MainViewModel
import com.skysam.hchirinos.diesan.ui.settings.SettingsActivity

class ProductsFragment : Fragment(), SearchView.OnQueryTextListener, ProductOnClick {

    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapterProduct: ProductAdapter
    private val products: MutableList<Product> = mutableListOf()
    private val productsToDelete = mutableListOf<Product>()
    private var listSearch: MutableList<Product> = mutableListOf()
    private val positionsToDelete = mutableListOf<Int>()
    private val listTempDelete = mutableListOf<ProductsToDelete>()
    private lateinit var search: SearchView
    private var actionMode: ActionMode? = null
    private var deleting = false

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

        adapterProduct = ProductAdapter(products, this)
        binding.rvProducts.apply {
            setHasFixedSize(true)
            adapter = adapterProduct
            addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0) {
                        binding.fab.hide()
                    } else {
                        binding.fab.show()
                    }
                    super.onScrolled(recyclerView, dx, dy)
                }
            })
        }
        binding.fab.setOnClickListener {
            actionMode?.finish()
            val addProductDialog = AddProductDialog()
            addProductDialog.show(requireActivity().supportFragmentManager, tag)
        }

        loadViewModel()
    }

    private fun loadViewModel() {
        viewModel.products.observe(viewLifecycleOwner) {
            if (_binding != null) {
                if (it.isEmpty()) {
                    binding.rvProducts.visibility = View.GONE
                    binding.tvListEmpty.visibility = View.VISIBLE
                } else {
                    if (!deleting) {
                        val listTemp = mutableListOf<Product>()
                        listTemp.addAll(products)
                        products.clear()
                        products.addAll(it)
                        for (proCloud in it) {
                            if (listSearch.isEmpty()) {
                                var add = true
                                for (proLocal in listTemp) {
                                    if (proLocal.id == proCloud.id) {
                                        add = false
                                        if (proCloud != proLocal) adapterProduct
                                            .notifyItemChanged(it.indexOf(proCloud))
                                    }
                                }
                                if (add) adapterProduct.notifyItemInserted(it.indexOf(proCloud))
                            } else {
                                for (proSearch in listSearch) {
                                    if (proSearch.id == proCloud.id) {
                                        adapterProduct
                                            .notifyItemChanged(listSearch.indexOf(proSearch))
                                    }
                                }
                            }
                        }
                        binding.rvProducts.visibility = View.VISIBLE
                        binding.tvListEmpty.visibility = View.GONE
                    }
                    deleting = false
                }
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        executeDelet()
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
        val userInput: String = newText!!.lowercase()
        listSearch.clear()

        if (newText != "") {
            for (product in products) {
                if (product.name.lowercase().contains(userInput)) {
                    listSearch.add(product)
                }
            }
            if (listSearch.isEmpty()) {
                binding.lottieAnimationView.visibility = View.VISIBLE
                binding.lottieAnimationView.playAnimation()
            } else {
                binding.lottieAnimationView.visibility = View.GONE
            }
            adapterProduct.updateList(listSearch)
        } else {
            adapterProduct.updateList(products)
            binding.lottieAnimationView.visibility = View.GONE
        }
        return false
    }

    override fun updateProduct(product: Product) {
        viewModel.productToEdit(product)
        val editProductDialog = EditProductDialog()
        editProductDialog.show(requireActivity().supportFragmentManager, tag)
    }

    override fun deleteProduct(product: Product) {
        val position = products.indexOf(product)
        if (!productsToDelete.contains(product)) {
            productsToDelete.add(product)
            positionsToDelete.add(position)
        } else {
            productsToDelete.remove(product)
            positionsToDelete.remove(position)
        }
        if (productsToDelete.size == 1 && actionMode == null) {
            actionMode = (activity as AppCompatActivity).startSupportActionMode(callback)
        }
        if (productsToDelete.isEmpty()) {
            actionMode?.finish()
        }
        actionMode?.title = "Seleccionado ${productsToDelete.size}"
    }

    private val callback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            requireActivity().menuInflater.inflate(R.menu.menu_delete_items, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return when (item?.itemId) {
                R.id.action_delete -> {
                    for (proDel in productsToDelete) {
                        adapterProduct.notifyItemRemoved(products.indexOf(proDel))
                        products.remove(proDel)
                    }
                    for (i in productsToDelete.indices) {
                        val producDelete = ProductsToDelete(
                            positionsToDelete[i],
                            productsToDelete[i]
                        )
                        listTempDelete.add(producDelete)
                    }
                    listTempDelete.sortBy { productsToDelete ->  productsToDelete.position }
                    binding.coordinator.visibility = View.VISIBLE
                    Snackbar.make(binding.coordinator, getString(R.string.text_deleting),
                        Snackbar.LENGTH_INDEFINITE)
                        .setDuration(3500)
                        .setAction(getString(R.string.text_undo)) {
                            if (listSearch.isEmpty()) {
                                for (i in listTempDelete) {
                                    products.add(i.position, i.product)
                                    adapterProduct.notifyItemInserted(i.position)
                                }
                            } else {
                                for (i in listTempDelete) {
                                    listSearch.add(i.position, i.product)
                                    adapterProduct.notifyItemInserted(i.position)
                                }
                            }
                            listTempDelete.clear()
                        }
                        .show()
                    actionMode?.finish()
                    Handler(Looper.getMainLooper()).postDelayed({
                        executeDelet()
                    }, 4000)
                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            actionMode = null
            search.onActionViewCollapsed()
            productsToDelete.clear()
            positionsToDelete.clear()
            listSearch.clear()
            adapterProduct.clearListToDelete()
        }
    }

    private fun executeDelet() {
        if (listTempDelete.isNotEmpty()) {
            val listToDelete = mutableListOf<Product>()
            for (list in listTempDelete) {
                listToDelete.add(list.product)
            }
            deleting = true
            viewModel.deleteProducts(listToDelete)
            listTempDelete.clear()
        }
    }
}