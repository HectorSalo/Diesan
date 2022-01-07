package com.skysam.hchirinos.diesan.common.dataClass

/**
 * Created by Hector Chirinos (Home) on 27/12/2021.
 */
data class Product(
    val id: String,
    var name: String,
    var price: Double = 1.0,
    var quantity: Int = 1
)
