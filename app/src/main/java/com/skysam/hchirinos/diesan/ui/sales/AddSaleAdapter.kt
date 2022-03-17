package com.skysam.hchirinos.diesan.ui.sales

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.skysam.hchirinos.diesan.R
import com.skysam.hchirinos.diesan.common.Class
import com.skysam.hchirinos.diesan.common.dataClass.Product
import com.skysam.hchirinos.diesan.ui.lots.ItemsNewLotAdapter

/**
 * Created by Hector Chirinos on 16/02/2022.
 */
class AddSaleAdapter(private val products: MutableList<Product>, private val onClick: AddSaleOnClick):
    RecyclerView.Adapter<AddSaleAdapter.ViewHolder>() {
    lateinit var context: Context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddSaleAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_item_new_sale, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddSaleAdapter.ViewHolder, position: Int) {
        val item = products[position]
        holder.name.text = item.name
        holder.price.text = context.getString(
            R.string.text_item_price,
            Class.convertDoubleToString(item.priceToSell * item.quantity))
        holder.unit.text = context.getString(
            R.string.text_item_unit,
            Class.convertDoubleToString(item.priceToSell), Class.convertIntToString(item.quantity))
        holder.checkBox.isChecked = item.isCheck

        holder.constraint.setOnClickListener { onClick.edit(item) }
        holder.buttonDelete.setOnClickListener {
            onClick.delete(item)
        }
        holder.checkBox.setOnClickListener {
            val isChecked = !item.isCheck
            onClick.check(item, isChecked)
        }
    }

    override fun getItemCount(): Int = products.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name)
        val unit: TextView = view.findViewById(R.id.tv_unit)
        val price: TextView = view.findViewById(R.id.tv_price)
        val buttonDelete: ImageButton = view.findViewById(R.id.ib_delete)
        val checkBox: CheckBox = view.findViewById(R.id.checkBox)
        val constraint: ConstraintLayout = view.findViewById(R.id.constraint)
    }
}