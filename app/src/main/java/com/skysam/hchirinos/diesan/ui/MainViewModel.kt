package com.skysam.hchirinos.diesan.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.diesan.common.dataClass.Lot
import com.skysam.hchirinos.diesan.common.dataClass.Product
import com.skysam.hchirinos.diesan.common.dataClass.Sale
import com.skysam.hchirinos.diesan.database.*

class MainViewModel : ViewModel() {
    val lots: LiveData<MutableList<Lot>> = LotsRepository.getLots().asLiveData()
    val lotsStock: LiveData<MutableList<Lot>> = StockRepository.getLotsFromStock().asLiveData()
    val products: LiveData<MutableList<Product>> = ProductRepository.getProducts().asLiveData()
    val sales: LiveData<MutableList<Sale>> = SaleRespository.getSales().asLiveData()
    val theme: LiveData<String> = PreferencesRepository.getThemeSaved().asLiveData()
    val notificationActive: LiveData<Boolean> = PreferencesRepository.getNotificationStatus().asLiveData()

    private val _productToEdit = MutableLiveData<Product>()
    val productToEdit: LiveData<Product> get() = _productToEdit

    private val _lotToView = MutableLiveData<Lot>()
    val lotToView: LiveData<Lot> get() = _lotToView
    
    private val _saleToView = MutableLiveData<Sale>()
    val saleToView: LiveData<Sale> get() = _saleToView

    fun uploadImage(uri: Uri): LiveData<String> {
        return ProductRepository.uploadImage(uri).asLiveData()
    }
    
    fun deleteOldImage(image: String) {
        ProductRepository.deleteImage(image)
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
    
    fun viewSale(sale: Sale) {
        _saleToView.value = sale
    }
    
    fun anulledSale(sale: Sale) {
        SaleRespository.anulledSale(sale)
    }
    
    fun updateStock(lot: Lot) {
        StockRepository.updateStock(lot)
    }
}