package com.skysam.hchirinos.diesan.ui.sales

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.skysam.hchirinos.diesan.R
import com.skysam.hchirinos.diesan.common.Class
import com.skysam.hchirinos.diesan.common.dataClass.Product
import com.skysam.hchirinos.diesan.ui.stock.ItemDetailsStockAdapter

/**
 * Created by Hector Chirinos on 17/02/2022.
 */
class ItemDetailsSaleAdapter(private val products: MutableList<Product>):
	RecyclerView.Adapter<ItemDetailsSaleAdapter.ViewHolder>() {
	lateinit var context: Context
	
	override fun onCreateViewHolder(
		parent: ViewGroup,
		viewType: Int
	): ItemDetailsSaleAdapter.ViewHolder {
		val view = LayoutInflater.from(parent.context)
			.inflate(R.layout.layout_item_details_sale, parent, false)
		context = parent.context
		return ViewHolder(view)
	}
	
	override fun onBindViewHolder(holder: ItemDetailsSaleAdapter.ViewHolder, position: Int) {
		val item = products[position]
		holder.name.text = item.name
		holder.amount.text = context.getString(
			R.string.text_item_price,
			Class.convertDoubleToString(item.priceToSell))
		holder.profit.text = context.getString(
			R.string.text_item_price,
			Class.convertDoubleToString(item.amountProfit * item.quantity))
		
		holder.quantity.text = item.quantity.toString()
	}
	
	override fun getItemCount(): Int = products.size
	
	inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
		val name: TextView = view.findViewById(R.id.tv_name)
		val quantity: TextView = view.findViewById(R.id.tv_quantity_sell)
		val amount: TextView = view.findViewById(R.id.tv_amount)
		val profit: TextView = view.findViewById(R.id.tv_profit)
	}
}