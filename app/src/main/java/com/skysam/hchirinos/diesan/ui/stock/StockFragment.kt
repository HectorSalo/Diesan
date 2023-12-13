package com.skysam.hchirinos.diesan.ui.stock

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.skysam.hchirinos.diesan.R
import com.skysam.hchirinos.diesan.common.Class

import com.skysam.hchirinos.diesan.common.dataClass.Product
import com.skysam.hchirinos.diesan.databinding.FragmentStockBinding
import com.skysam.hchirinos.diesan.ui.sales.AddSaleActivity


class StockFragment : Fragment(), ItemStockOnClick, MenuProvider {
    private var _binding: FragmentStockBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StockViewModel by activityViewModels()
    private var actionMode: ActionMode? = null
    private lateinit var adapterItems: ItemDetailsStockAdapter
    private val products = mutableListOf<Product>()
    private val productsToShare = mutableListOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStockBinding.inflate(inflater, container, false)
        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
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
        adapterItems = ItemDetailsStockAdapter(products, this)
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
    
    @SuppressLint("NotifyDataSetChanged")
    private fun loadViewModel() {
        viewModel.productsFromLots.observe(viewLifecycleOwner) {
            if (_binding != null) {
                products.clear()
                products.addAll(it)
                if (products.isEmpty()) {
                    binding.rvStock.visibility = View.GONE
                    binding.tvListEmpty.visibility = View.VISIBLE
                } else {
                    binding.rvStock.visibility = View.VISIBLE
                    binding.tvListEmpty.visibility = View.GONE
                    adapterItems.notifyDataSetChanged()
                }
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun getOut() {
        requireActivity().finish()
    }

    private fun share(list: MutableList<Product>, allShare: Boolean) {
        val emojiPin = String(Character.toChars(0x1F4CC))
        val emojiHeart = String(Character.toChars(0x2764))
        val selection = StringBuilder()
        if (allShare) {
            selection.append("Buenas, productos disponibles: ").append("\n\n")
        }
        for (item in list) {
            if (!selection.contains(item.name)) selection.append("$emojiPin ${item.name}." +
                    " Precio: $${Class.convertDoubleToString(item.priceToSell)}").append("\n\n")
        }
        if (allShare) {
            selection.append("\n\n")
                .append("Hacemos las entregas personales con *previo acuerdo* en la plaza Madariaga del Paraíso y en la Clínica Popular del Paraíso (lunes a sábado)\n" +
                        "\n" +
                        "También contamos con servicio de delivery, *tiene un costo adicional dependiendo de la zona*\n" +
                        "\n" +
                        "Si tu pedido llega a \$50 el delivery sale gratuito.\n" +
                        "\n" +
                        "Síguenos en Instagram @distribuidoradiesan")
                .append(emojiHeart)
        }
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, selection.toString())
        startActivity(Intent.createChooser(intent, getString(R.string.title_share_dialog)))
    }
    
    override fun onClick(product: Product) {
        if (productsToShare.isEmpty()) {
            viewModel.viewProduct(product)
            val editStockDialog = EditStockDialog()
            editStockDialog.show(requireActivity().supportFragmentManager, tag)
            return
        }
        
        if (productsToShare.contains(product)) {
            productsToShare.remove(product)
            if (productsToShare.isEmpty()) (activity as AppCompatActivity)
                .startSupportActionMode(callback)!!.finish()
        } else {
            productsToShare.add(product)
        }
        actionMode?.title = "Seleccionado ${productsToShare.size}"
    }
    
    override fun longClick(product: Product) {
        if (productsToShare.isEmpty()) {
            actionMode = (activity as AppCompatActivity).startSupportActionMode(callback)
        }
        if (productsToShare.contains(product)) {
            productsToShare.remove(product)
            if (productsToShare.isEmpty()) (activity as AppCompatActivity)
                .startSupportActionMode(callback)!!.finish()
        } else {
            productsToShare.add(product)
        }
        actionMode?.title = "Seleccionado ${productsToShare.size}"
    }
    
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.stock, menu)
    }
    
    override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
        android.R.id.home -> {
            getOut()
            true
        }
        R.id.action_list_stock -> {
            findNavController().navigate(R.id.action_FragmentStock_to_listLotsFragment)
            true
        }
        R.id.action_share -> {
            share(products, true)
            true
        }
        R.id.action_add_sale -> {
            startActivity(Intent(requireContext(), AddSaleActivity::class.java))
            true
        }
        else -> false
    }
    
    private val callback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            requireActivity().menuInflater.inflate(R.menu.contextual_action_bar, menu)
            return true
        }
    
        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }
    
        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return when (item?.itemId) {
                R.id.action_share -> {
                    share(productsToShare, false)
                    true
                }
                else -> false
            }
        }
    
        override fun onDestroyActionMode(mode: ActionMode?) {
            productsToShare.clear()
            adapterItems.updateList(products)
        }
    
    }
}