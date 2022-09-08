package com.skysam.hchirinos.diesan.ui.stock

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.skysam.hchirinos.diesan.R
import com.skysam.hchirinos.diesan.common.Class

import com.skysam.hchirinos.diesan.common.dataClass.Product
import com.skysam.hchirinos.diesan.databinding.FragmentStockBinding
import com.skysam.hchirinos.diesan.ui.sales.AddSaleActivity


class StockFragment : Fragment() {
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
                startActivity(Intent(requireContext(), AddSaleActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun loadViewModel() {
        viewModel.productsFromLots.observe(viewLifecycleOwner) {
            if (_binding != null) {
                products.clear()
                products.addAll(it)
                products.sortBy { product -> product.name }
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

    private fun share() {
        val emojiPin = String(Character.toChars(0x1F4CC))
        val emojiHeart = String(Character.toChars(0x2764))
        val selection = StringBuilder()
        selection.append("Buenas, productos disponibles: ")
        for (item in products) {
            selection.append("\n\n").append("$emojiPin ${item.name}. Precio: $${Class.convertDoubleToString(item.priceToSell)}")
        }
        selection.append("\n\n")
            .append("Hacemos las entregas personales con *previo acuerdo* en la plaza Madariaga del Paraíso y en la Clínica Popular del Paraíso (lunes a sábado)\n" +
                    "\n" +
                    "También contamos con servicio de delivery, *tiene un costo adicional dependiendo de la zona*\n" +
                    "\n" +
                    "Si tu pedido llega a \$70 el delivery sale gratuito.\n" +
                    "\n" +
                    "Síguenos en Instagram @distribuidoradiesan")
            .append(emojiHeart)
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, selection.toString())
        startActivity(Intent.createChooser(intent, getString(R.string.title_share_dialog)))
    }
}