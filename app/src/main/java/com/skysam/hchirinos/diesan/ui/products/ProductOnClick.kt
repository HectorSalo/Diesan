package com.skysam.hchirinos.diesan.ui.products

import com.skysam.hchirinos.diesan.common.dataClass.Product

/**
 * Created by Hector Chirinos on 12/02/2022.
 */
interface ProductOnClick {
 fun updateProduct(product: Product)
 fun deleteProduct(position: Int, product: Product)
}