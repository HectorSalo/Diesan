package com.skysam.hchirinos.diesan.ui.stock

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.skysam.hchirinos.diesan.R
import com.skysam.hchirinos.diesan.common.Class
import com.skysam.hchirinos.diesan.common.dataClass.Lot

/**
 * Created by Hector Chirinos on 14/02/2022.
 */
class StockAdapter(private val lots: MutableList<Lot>, private val onClick: StockOnClick):
 RecyclerView.Adapter<StockAdapter.ViewHolder>() {
 lateinit var context: Context

 override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockAdapter.ViewHolder {
  val view = LayoutInflater.from(parent.context)
   .inflate(R.layout.layout_item_stock, parent, false)
  context = parent.context
  return ViewHolder(view)
 }

 override fun onBindViewHolder(holder: StockAdapter.ViewHolder, position: Int) {
  val item = lots[position]
  holder.number.text = context.getString(R.string.text_number_lot_item, item.numberLot.toString())
  holder.items.text = context.getString(R.string.text_items_remain, item.products.size.toString())
  var profitRemain = 0.0
  for (pro in item.products) {
   profitRemain += (pro.amountProfit * pro.quantity)
  }
  holder.profitRemain.text = context.getString(R.string.text_profit_remain,
   Class.convertDoubleToString(profitRemain))

  holder.sell.setOnClickListener { onClick.sell(item) }
  holder.share.setOnClickListener { onClick.share(item) }
  holder.card.setOnClickListener { onClick.viewDetail(item) }
 }

 override fun getItemCount(): Int = lots.size

 inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
  val number: TextView = view.findViewById(R.id.tv_number)
  val items: TextView = view.findViewById(R.id.tv_items)
  val profitRemain: TextView = view.findViewById(R.id.tv_profit_remain)
  val sell: ImageButton = view.findViewById(R.id.ib_sell)
  val share: ImageButton = view.findViewById(R.id.ib_share)
  val card: MaterialCardView = view.findViewById(R.id.card)
 }
}