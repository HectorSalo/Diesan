package com.skysam.hchirinos.diesan.common.dataClass

import java.util.*

/**
 * Created by Hector Chirinos on 16/02/2022.
 */
data class Sale(
 val id: String,
 var date: Date,
 var customer: String,
 var products: MutableList<Product>
 )
