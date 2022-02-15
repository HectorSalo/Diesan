package com.skysam.hchirinos.diesan.ui.lots

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.skysam.hchirinos.diesan.R
import com.skysam.hchirinos.diesan.common.Class
import com.skysam.hchirinos.diesan.common.dataClass.Lot

/**
 * Created by Hector Chirinos on 18/01/2022.
 */
class LotsAdapter(private val lots: MutableList<Lot>, private val lotOnClick: LotOnClick):
    RecyclerView.Adapter<LotsAdapter.ViewHolder>() {
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LotsAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_item_lot, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: LotsAdapter.ViewHolder, position: Int) {
        val item = lots[position]
        holder.number.text = context.getString(R.string.text_number_lot_item, item.numberLot.toString())
        holder.date.text = context.getString(R.string.text_date_item, Class.convertDateToString(item.date))
        holder.price.text = context.getString(R.string.text_total_dolar, Class.convertDoubleToString(item.ship))

        holder.card.setOnClickListener { lotOnClick.viewLot(item) }
    }

    override fun getItemCount(): Int = lots.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val number: TextView = view.findViewById(R.id.tv_number)
        val date: TextView = view.findViewById(R.id.tv_date)
        val price: TextView = view.findViewById(R.id.tv_price)
        val card: MaterialCardView = view.findViewById(R.id.card)
    }
}