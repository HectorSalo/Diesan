package com.skysam.hchirinos.diesan.ui.products

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.skysam.hchirinos.diesan.R
import com.skysam.hchirinos.diesan.common.dataClass.Product

/**
 * Created by Hector Chirinos (Home) on 27/12/2021.
 */
class ProductAdapter(private var products: MutableList<Product>):
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    private lateinit var context: Context

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name)
        val image: ImageView = view.findViewById(R.id.iv_image)
        val card: MaterialCardView = view.findViewById(R.id.card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_product_item, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = products[position]
        holder.name.text = item.name
        Glide.with(context)
            .load(item.image)
            .centerCrop()
            .placeholder(R.drawable.ic_add_a_photo_232)
            .into(holder.image)
    }

    override fun getItemCount(): Int = products.size
}