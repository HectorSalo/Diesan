package com.skysam.hchirinos.diesan.ui.sales

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.skysam.hchirinos.diesan.R
import com.skysam.hchirinos.diesan.common.Class
import com.skysam.hchirinos.diesan.common.dataClass.Sale

/**
 * Created by Hector Chirinos on 16/02/2022.
 */
class SalesAdapter(private val sales: MutableList<Sale>, private val onClick: SalesOnClick):
 RecyclerView.Adapter<SalesAdapter.ViewHolder>() {
 lateinit var context: Context

 override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalesAdapter.ViewHolder {
  val view = LayoutInflater.from(parent.context)
   .inflate(R.layout.layout_item_sale, parent, false)
  context = parent.context
  return ViewHolder(view)
 }

 override fun onBindViewHolder(holder: SalesAdapter.ViewHolder, position: Int) {
  val item = sales[position]
  var total = 0.0
  for (pro in item.products) {
   total += (pro.quantity * pro.priceToSell)
  }
  holder.total.text = context.getString(R.string.text_total_dolar, Class.convertDoubleToString(total))
  holder.date.text = context.getString(R.string.text_date_sale_item, Class.convertDateToString(item.date))
  if (item.customer.isNotEmpty()) {
   holder.customer.visibility = View.VISIBLE
   holder.customer.text = context.getString(R.string.text_customer_sale_item, item.customer)
  } else holder.customer.visibility = View.GONE

  holder.card.setOnClickListener { onClick.viewDetail(item) }
 }

 override fun getItemCount(): Int = sales.size

 inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
  val total: TextView = view.findViewById(R.id.tv_total)
  val date: TextView = view.findViewById(R.id.tv_date)
  val customer: TextView = view.findViewById(R.id.tv_customer)
  val card: MaterialCardView = view.findViewById(R.id.card)
 }
}