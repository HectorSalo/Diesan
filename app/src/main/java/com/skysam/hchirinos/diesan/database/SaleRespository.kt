package com.skysam.hchirinos.diesan.database

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.skysam.hchirinos.diesan.common.Class
import com.skysam.hchirinos.diesan.common.Constants
import com.skysam.hchirinos.diesan.common.dataClass.Product
import com.skysam.hchirinos.diesan.common.dataClass.Sale
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Created by Hector Chirinos on 16/02/2022.
 */
object SaleRespository {
 private val PATH_SALE = when(Class.getEnviroment()) {
  Constants.DEMO -> Constants.SALE_DEMO
  Constants.RELEASE -> Constants.SALE
  else -> Constants.SALE
 }
 
 private fun getInstance(): CollectionReference {
  return FirebaseFirestore.getInstance().collection(PATH_SALE)
 }

 fun addSale(sale: Sale) {
  val data = hashMapOf(
   Constants.DATE to sale.date,
   Constants.CUSTOMER to sale.customer,
   Constants.PRODUCTS to sale.products,
   Constants.DELIVERY to sale.delivery
  )
  getInstance().add(data)
 }

 fun getSales(): Flow<MutableList<Sale>> {
  return callbackFlow {
   val request = getInstance()
    .orderBy(Constants.DATE, Query.Direction.DESCENDING)
    .addSnapshotListener { value, error ->
     if (error != null) {
      Log.w(ContentValues.TAG, "Listen failed.", error)
      return@addSnapshotListener
     }

     val sales = mutableListOf<Sale>()
     for (sale in value!!) {
      val products = mutableListOf<Product>()
      var anulled = false
      var delivery = 0.0
      if (sale.get(Constants.PRODUCTS) != null) {
       @Suppress("UNCHECKED_CAST")
       val list = sale.data.getValue(Constants.PRODUCTS) as MutableList<HashMap<String, Any>>
       for (item in list) {
        val prod = Product(
         item[Constants.ID].toString(),
         item[Constants.NAME].toString(),
         item[Constants.PRICE].toString().toDouble(),
         item[Constants.QUANTITY].toString().toInt(),
         item[Constants.SHIP].toString().toDouble(),
         item[Constants.TAX].toString().toDouble(),
         item[Constants.SUM_TOTAL].toString().toDouble(),
         item[Constants.PRICE_BY_UNIT].toString().toDouble(),
         item[Constants.PERCENTAGE_PROFIT].toString().toDouble(),
         item[Constants.PRICE_TO_SELL].toString().toDouble(),
         item[Constants.AMOUNT_PROFIT].toString().toDouble(),
         item[Constants.IMAGE].toString()
        )
        products.add(prod)
       }
      }
      if (sale.getBoolean(Constants.IS_ANULLED) != null) anulled = sale.getBoolean(Constants.IS_ANULLED)!!
      if (sale.getDouble(Constants.DELIVERY) != null) delivery = sale.getDouble(Constants.DELIVERY)!!
      val saleNew = Sale(
       sale.id,
       sale.getDate(Constants.DATE)!!,
       sale.getString(Constants.CUSTOMER)!!,
       products,
       anulled,
       delivery
      )
      sales.add(saleNew)
     }
     trySend(sales)
    }
   awaitClose { request.remove() }
  }
 }
 
 fun anulledSale(sale: Sale) {
  getInstance()
   .document(sale.id)
   .update(Constants.IS_ANULLED, true)
 }
}