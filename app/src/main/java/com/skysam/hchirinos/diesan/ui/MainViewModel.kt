package com.skysam.hchirinos.diesan.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.diesan.common.dataClass.Lot
import com.skysam.hchirinos.diesan.common.dataClass.Product
import com.skysam.hchirinos.diesan.database.LotsRepository
import com.skysam.hchirinos.diesan.database.ProductRepository

class MainViewModel : ViewModel() {
    val lots: LiveData<MutableList<Lot>> = LotsRepository.getLots().asLiveData()
    val products: LiveData<MutableList<Product>> = ProductRepository.getProducts().asLiveData()

    private val _productToEdit = MutableLiveData<Product>()
    val productToEdit: LiveData<Product> get() = _productToEdit

    private val _lotToView = MutableLiveData<Lot>()
    val lotToView: LiveData<Lot> get() = _lotToView

    fun uploadImage(uri: Uri): LiveData<String> {
        return ProductRepository.uploadImage(uri).asLiveData()
    }

    fun saveProduct(product: Product) {
        ProductRepository.saveProduct(product)
    }

    fun productToEdit(product: Product) {
        _productToEdit.value = product
    }

    fun updateProduct(product: Product) {
        ProductRepository.updateProduct(product)
    }

    fun deleteProducts(products: MutableList<Product>) {
        ProductRepository.deleteProducts(products)
    }

    fun viewLot(lot: Lot) {
        _lotToView.value = lot
    }
}