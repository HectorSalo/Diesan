package com.skysam.hchirinos.diesan.ui.lots

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.skysam.hchirinos.diesan.R
import com.skysam.hchirinos.diesan.common.Class
import com.skysam.hchirinos.diesan.common.dataClass.Product

/**
 * Created by Hector Chirinos on 08/01/2022.
 */
class ItemsDetailsNewLotAdapter(private val products: MutableList<Product>):
    RecyclerView.Adapter<ItemsDetailsNewLotAdapter.ViewHolder>() {
    lateinit var context: Context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemsDetailsNewLotAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_product_details_new_lot_item, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemsDetailsNewLotAdapter.ViewHolder, position: Int) {
        val item = products[position]
        holder.name.text = item.name
        val total = (item.price + item.ship + item.tax) * item.quantity
        holder.total.text = context.getString(R.string.text_item_price,
            Class.convertDoubleToString(total))
        holder.price.text = context.getString(R.string.text_item_price,
            Class.convertDoubleToString(total / item.quantity))
        val priceToSell = ((total / item.quantity) * (item.percentageProfit / 100)) + (total / item.quantity)
        holder.amount.text = context.getString(R.string.text_item_price,
            Class.convertDoubleToString(priceToSell))
        holder.profit.text = context.getString(R.string.text_item_price,
            Class.convertDoubleToString(priceToSell - (total / item.quantity)))
    }

    override fun getItemCount(): Int = products.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name)
        val total: TextView = view.findViewById(R.id.tv_total)
        val price: TextView = view.findViewById(R.id.tv_price)
        val amount: TextView = view.findViewById(R.id.tv_amount)
        val profit: TextView = view.findViewById(R.id.tv_profit)
    }
}