package com.skysam.hchirinos.diesan.ui.stock

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.skysam.hchirinos.diesan.R
import com.skysam.hchirinos.diesan.common.Class
import com.skysam.hchirinos.diesan.common.dataClass.Product

/**
 * Created by Hector Chirinos on 15/02/2022.
 */
class ItemDetailsStockAdapter(private var products: MutableList<Product>, private val onClick: ItemStockOnClick):
 RecyclerView.Adapter<ItemDetailsStockAdapter.ViewHolder>() {
 private lateinit var context: Context
 private val listShare = mutableListOf<Product>()

 override fun onCreateViewHolder(
  parent: ViewGroup,
  viewType: Int
 ): ItemDetailsStockAdapter.ViewHolder {
  val view = LayoutInflater.from(parent.context)
   .inflate(R.layout.layout_item_details_stock, parent, false)
  context = parent.context
  return ViewHolder(view)
 }

 override fun onBindViewHolder(holder: ItemDetailsStockAdapter.ViewHolder, position: Int) {
  val item = products[position]
  holder.name.text = item.name
  holder.amount.text = context.getString(
   R.string.text_item_price,
   Class.convertDoubleToString(item.priceToSell))
  holder.profit.text = context.getString(
   R.string.text_item_price,
   Class.convertDoubleToString(item.amountProfit))
  holder.quantityRemain.text = item.quantity.toString()
  
  holder.card.isChecked = listShare.contains(item)
  
  holder.card.setOnClickListener {
   onClick.onClick(item)
   if (listShare.isNotEmpty()) {
    holder.card.isChecked = !holder.card.isChecked
    if (listShare.contains(item)) listShare.remove(item) else listShare.add(item)
   }
  }
  
  holder.card.setOnLongClickListener {
   onClick.longClick(item)
   holder.card.isChecked = !holder.card.isChecked
   if (listShare.contains(item)) listShare.remove(item) else listShare.add(item)
   true
  }
 }

 override fun getItemCount(): Int = products.size

 inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
  val name: TextView = view.findViewById(R.id.tv_name)
  val quantityRemain: TextView = view.findViewById(R.id.tv_quantity_remain)
  val amount: TextView = view.findViewById(R.id.tv_amount)
  val profit: TextView = view.findViewById(R.id.tv_profit)
  val card: MaterialCardView = view.findViewById(R.id.constraint)
 }
 
 fun updateList(newList: MutableList<Product>) {
  products = newList
  listShare.clear()
  notifyItemRangeChanged(0, products.size)
 }
}