package com.skysam.hchirinos.diesan.ui.lots

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skysam.hchirinos.diesan.common.dataClass.Product

class NewLotViewModel : ViewModel() {
    private val _products = MutableLiveData<MutableList<Product>>().apply {
        value = mutableListOf()
    }
    val products: LiveData<MutableList<Product>> = _products

    private val _ship = MutableLiveData<Double>().apply {
        value = 0.00
    }
    val ship: LiveData<Double> = _ship

    fun addProduct(product: Product) {
        _products.value?.add(product)
        _products.value = _products.value
    }
    fun removeProduct(product: Product) {
        _products.value?.remove(product)
        _products.value = _products.value
    }
    private val _total = MutableLiveData<Double>().apply {
        value = 0.0
    }
    val total: LiveData<Double> = _total

    fun addTotal(value: Double) {
        _total.value = _total.value!! + value
    }

    fun restTotal(value: Double) {
        _total.value = _total.value!! - value
    }

    fun valueShip(value: Double) {
        _ship.value = value
    }
}