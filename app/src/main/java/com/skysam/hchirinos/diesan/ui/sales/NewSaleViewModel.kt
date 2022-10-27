package com.skysam.hchirinos.diesan.ui.sales

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
 * Created by Hector Chirinos on 14/03/2022.
 */

class NewSaleViewModel: ViewModel() {
	val productsFromLots: LiveData<MutableList<Product>> = StockRepository.getProductFromStock().asLiveData()
	val lots: LiveData<MutableList<Lot>> = StockRepository.getLotsFromStock().asLiveData()
	
	private val _productsToSell =
		MutableLiveData<MutableList<Product>>().apply { value = mutableListOf() }
	val productsToSell: LiveData<MutableList<Product>> get() = _productsToSell
	
	private val _total = MutableLiveData<Double>().apply {
		value = 0.0
	}
	val total: LiveData<Double> = _total
	
	private val _productToChangePrice = MutableLiveData<Product>()
	val productToChangePrice: LiveData<Product> = _productToChangePrice
	
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
				pro.priceToSell = product.priceToSell
				pro.isCheck = product.isCheck
			}
		}
		_productsToSell.value = _productsToSell.value
	}
	
	fun productToChangePrice(product: Product) {
		_productToChangePrice.value = product
	}
	
	private fun addTotal(value: Double) {
		_total.value = _total.value!! + value
	}
	
	private fun restTotal(value: Double) {
		_total.value = _total.value!! - value
	}
	
	fun saveSale(sale: Sale) {
		SaleRespository.addSale(sale)
	}
	
	fun updateStock(lot: Lot) {
		if (lot.products.isEmpty()) StockRepository.deleteStock(lot)
		else StockRepository.updateStock(lot)
	}
}