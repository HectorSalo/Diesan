package com.skysam.hchirinos.diesan.ui.lots

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.diesan.common.dataClass.Lot
import com.skysam.hchirinos.diesan.common.dataClass.Product
import com.skysam.hchirinos.diesan.database.LotsRepository
import com.skysam.hchirinos.diesan.database.ProductRepository
import com.skysam.hchirinos.diesan.database.StockRepository

class NewLotViewModel : ViewModel() {
    val productsOlder: LiveData<MutableList<Product>> = ProductRepository.getProducts().asLiveData()
    val lots: LiveData<MutableList<Lot>> = LotsRepository.getLots().asLiveData()

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

    fun sendNewLot(lot: Lot, productsOlder: MutableList<Product>) {
        for (pro in lot.products) {
            var exists = false
            for (prod in productsOlder) {
                if (pro.id == prod.id) exists = true
            }
            if (exists) ProductRepository.updateProduct(pro)
            else ProductRepository.saveProduct(pro)
        }
        LotsRepository.addLot(lot)
        StockRepository.addLotToSock(lot)
    }
}