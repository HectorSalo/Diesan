package com.skysam.hchirinos.diesan.common.dataClass

import java.util.*

/**
 * Created by Hector Chirinos on 17/01/2022.
 */
data class Lot(
    val id: String,
    var numberLot: Int,
    var date: Date,
    var ship: Double,
    var products: MutableList<Product>
)
