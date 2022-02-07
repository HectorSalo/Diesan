package com.skysam.hchirinos.diesan.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.diesan.common.dataClass.Product
import com.skysam.hchirinos.diesan.database.ProductRepository

class MainViewModel : ViewModel() {
    val product: LiveData<Product?> = ProductRepository.getProducts().asLiveData()

    private val _products = MutableLiveData<MutableList<Product>>().apply {
        value = mutableListOf()
    }
    val products: LiveData<MutableList<Product>> get() = _products

    fun uploadImage(uri: Uri): LiveData<String> {
        return ProductRepository.uploadImage(uri).asLiveData()
    }

    fun saveProduct(product: Product) {
        ProductRepository.saveProduct(product)
    }

    fun addProductToList(product: Product) {
        if (!_products.value!!.contains(product)) {
            _products.value!!.add(product)
            _products.value = _products.value
        }
    }

    fun updateProductToList(product: Product, position: Int) {
        if (_products.value!!.contains(product)) {
            _products.value!![position] = product
            _products.value = _products.value
        }
    }

    fun deleteProductFromList(product: Product) {
        if (_products.value!!.contains(product)) {
            _products.value!!.remove(product)
            _products.value = _products.value
        }
    }
}