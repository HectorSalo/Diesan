package com.skysam.hchirinos.diesan.ui.lots

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.skysam.hchirinos.diesan.R
import com.skysam.hchirinos.diesan.common.Class
import com.skysam.hchirinos.diesan.common.dataClass.Product

/**
 * Created by Hector Chirinos on 06/01/2022.
 */
class ItemsNewLotAdapter(private val products: MutableList<Product>, private val onClickInterface: OnClickInterface):
    RecyclerView.Adapter<ItemsNewLotAdapter.ViewHolder>() {

    lateinit var context: Context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemsNewLotAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_item_new_lot, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemsNewLotAdapter.ViewHolder, position: Int) {
        val item = products[position]
        holder.name.text = item.name
        holder.price.text = context.getString(R.string.text_item_price,
            Class.convertDoubleToString((item.price + item.ship + item.tax) * item.quantity))
        holder.unit.text = context.getString(R.string.text_item_unit,
            Class.convertDoubleToString(item.price + item.ship + item.tax), Class.convertIntToString(item.quantity))

        holder.buttonDelete.setOnClickListener {
            onClickInterface.delete(item)
        }
    }

    override fun getItemCount(): Int = products.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name)
        val unit: TextView = view.findViewById(R.id.tv_unit)
        val price: TextView = view.findViewById(R.id.tv_price)
        val buttonDelete: ImageButton = view.findViewById(R.id.ib_delete)
    }
}