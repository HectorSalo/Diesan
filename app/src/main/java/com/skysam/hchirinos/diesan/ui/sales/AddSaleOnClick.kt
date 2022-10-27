package com.skysam.hchirinos.diesan.ui.sales

import com.skysam.hchirinos.diesan.common.dataClass.Product

/**
 * Created by Hector Chirinos on 16/02/2022.
 */
interface AddSaleOnClick {
 fun check(product: Product, isCheck: Boolean)
 fun delete(product: Product)
 fun editQuantity(product: Product)
 fun editPrice(product: Product)
}