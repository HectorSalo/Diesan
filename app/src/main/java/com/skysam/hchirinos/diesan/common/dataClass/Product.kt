package com.skysam.hchirinos.diesan.common.dataClass

/**
 * Created by Hector Chirinos (Home) on 27/12/2021.
 */
data class Product(
    val id: String,
    var name: String,
    var price: Double = 1.0,
    var quantity: Int = 1,
    var ship: Double = 0.0,
    var tax: Double = 0.0,
    var sumTotal: Double = 0.0,
    var priceByUnit: Double = 0.0,
    var percentageProfit: Double = 0.0,
    var priceToSell: Double = 0.0,
    var amountProfit: Double = 0.0,
    var image: String
)
