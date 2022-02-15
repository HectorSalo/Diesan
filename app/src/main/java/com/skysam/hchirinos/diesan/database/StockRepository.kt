package com.skysam.hchirinos.diesan.database

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.skysam.hchirinos.diesan.common.Constants
import com.skysam.hchirinos.diesan.common.dataClass.Lot
import com.skysam.hchirinos.diesan.common.dataClass.Product
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Created by Hector Chirinos on 14/02/2022.
 */
object StockRepository {
 private fun getInstance(): CollectionReference {
  return FirebaseFirestore.getInstance().collection(Constants.STOCK)
 }

 fun addLotToSock(lot: Lot) {
  val data = hashMapOf(
   Constants.NUMBER_LOT to lot.numberLot,
   Constants.DATE to lot.date,
   Constants.SHIP to lot.ship,
   Constants.PRODUCTS to lot.products
  )
  getInstance().add(data)
 }

 fun getLotsFromStock(): Flow<MutableList<Lot>> {
  return callbackFlow {
   val request = getInstance()
    .orderBy(Constants.DATE, Query.Direction.DESCENDING)
    .addSnapshotListener { value, error ->
     if (error != null) {
      Log.w(ContentValues.TAG, "Listen failed.", error)
      return@addSnapshotListener
     }

     val lots = mutableListOf<Lot>()
     for (lot in value!!) {
      val products = mutableListOf<Product>()
      if (lot.get(Constants.PRODUCTS) != null) {
       @Suppress("UNCHECKED_CAST")
       val list = lot.data.getValue(Constants.PRODUCTS) as MutableList<HashMap<String, Any>>
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
      val lotNew = Lot(
       lot.id,
       lot.getDouble(Constants.NUMBER_LOT)!!.toInt(),
       lot.getDate(Constants.DATE)!!,
       lot.getDouble(Constants.SHIP)!!,
       products
      )
      lots.add(lotNew)
     }
     trySend(lots)
    }
   awaitClose { request.remove() }
  }
 }
}