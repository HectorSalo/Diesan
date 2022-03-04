package com.skysam.hchirinos.diesan.ui.stock

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.diesan.common.dataClass.Lot
import com.skysam.hchirinos.diesan.common.dataClass.Product
import com.skysam.hchirinos.diesan.common.dataClass.Sale
import com.skysam.hchirinos.diesan.database.SaleRespository
import com.skysam.hchirinos.diesan.database.StockRepository

/**
 * Created by Hector Chirinos on 14/02/2022.
 */
class StockViewModel: ViewModel() {
 val lots: LiveData<MutableList<Lot>> = StockRepository.getLotsFromStock().asLiveData()
 val productsFromLots: LiveData<MutableList<Product>> = StockRepository.getProductFromStock().asLiveData()
 
 private val _lotToView = MutableLiveData<Lot>()
 val lotToView: LiveData<Lot> get() = _lotToView
 
 private val _lotToSell = MutableLiveData<Lot>()
 val lotToSell: LiveData<Lot> get() = _lotToSell
 
 private val _productsToSell =
  MutableLiveData<MutableList<Product>>().apply { value = mutableListOf() }
 val productsToSell: LiveData<MutableList<Product>> get() = _productsToSell
 
 private val _productsStock =
  MutableLiveData<MutableList<Product>>().apply { value = mutableListOf() }
 val productsStock: LiveData<MutableList<Product>> get() = _productsStock
 
 private val _total = MutableLiveData<Double>().apply {
  value = 0.0
 }
 val total: LiveData<Double> = _total
 
 fun viewLot(lot: Lot) {
  _lotToView.value = lot
 }
 
 fun lotToSell(lot: Lot) {
  _lotToSell.value = lot
 }
 
 fun addProducToSell(product: Product) {
  if (!_productsToSell.value!!.contains(product)) {
   _productsToSell.value!!.add(product)
   _productsToSell.value = _productsToSell.value
   addTotal(product.priceToSell)
  }
 }
 
 fun removeProducToSell(product: Product) {
  if (_productsToSell.value!!.contains(product)) {
   _productsToSell.value!!.remove(product)
   _productsToSell.value = _productsToSell.value
   restTotal(product.priceToSell * product.quantity)
  }
 }
 
 fun editProductToSell(product: Product) {
  for (pro in _productsToSell.value!!) {
   if (pro.id == product.id) {
    restTotal(pro.quantity * pro.priceToSell)
    addTotal(product.quantity * product.priceToSell)
    pro.quantity = product.quantity
   }
  }
  _productsToSell.value = _productsToSell.value
 }
 
 private fun addTotal(value: Double) {
  _total.value = _total.value!! + value
 }
 
 private fun restTotal(value: Double) {
  _total.value = _total.value!! - value
 }
 
 fun saveSale(sale: Sale, lot: Lot) {
  SaleRespository.addSale(sale)
  if (lot.products.isEmpty()) StockRepository.deleteStock(lot)
  else StockRepository.updateStock(lot)
  clearNewSale()
 }
 
 fun clearNewSale() {
  _productsToSell.value?.clear()
  _total.value = 0.0
 }
}