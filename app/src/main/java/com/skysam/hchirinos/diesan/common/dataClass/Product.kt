package com.skysam.hchirinos.diesan.common.dataClass

/**
 * Created by Hector Chirinos (Home) on 27/12/2021.
 */
data class Product(
    val id: String,
    var name: String,
    var unit: String,
    var quantity: Double = 1.0
)
