package com.skysam.hchirinos.diesan.ui.stock

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.diesan.common.dataClass.Lot
import com.skysam.hchirinos.diesan.common.dataClass.Product
import com.skysam.hchirinos.diesan.database.StockRepository

/**
 * Created by Hector Chirinos on 14/02/2022.
 */
class StockViewModel: ViewModel() {
 val lots: LiveData<MutableList<Lot>> = StockRepository.getLotsFromStock().asLiveData()
 val productsFromLots: LiveData<MutableList<Product>> = StockRepository.getProductFromStock().asLiveData()
 
 private val _lotToView = MutableLiveData<Lot>()
 val lotToView: LiveData<Lot> get() = _lotToView
 
 private val _productToChangeStock = MutableLiveData<Product>()
 val productToChangeStock: LiveData<Product> = _productToChangeStock
 
 fun viewLot(lot: Lot) {
  _lotToView.value = lot
 }
 
 fun viewProduct(product: Product) {
  _productToChangeStock.value = product
 }
 
 fun updateStock(lot: Lot) {
  if (lot.products.isEmpty()) StockRepository.deleteStock(lot)
  else StockRepository.updateStock(lot)
 }
}