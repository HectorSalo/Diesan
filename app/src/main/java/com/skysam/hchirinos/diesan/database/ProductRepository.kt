package com.skysam.hchirinos.diesan.database

import android.content.ContentValues
import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.skysam.hchirinos.diesan.R
import com.skysam.hchirinos.diesan.common.Class
import com.skysam.hchirinos.diesan.common.Constants
import com.skysam.hchirinos.diesan.common.Diesan
import com.skysam.hchirinos.diesan.common.dataClass.Product
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.*

/**
 * Created by Hector Chirinos on 18/01/2022.
 */
object ProductRepository {
    private const val PATH = "${Constants.PRODUCTS}/"

    private fun getInstanceStorage(): StorageReference {
        return Firebase.storage.reference.child(PATH)
    }

    private fun getInstanceFirestore(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(Constants.PRODUCTS)
    }

    fun uploadImage(uri: Uri): Flow<String> {
        return callbackFlow {
            getInstanceStorage()
                .child(uri.lastPathSegment!!)
                .putFile(uri)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        getInstanceStorage().child(uri.lastPathSegment!!)
                            .downloadUrl.addOnSuccessListener {
                                trySend(it.toString())
                            }
                    } else {
                        trySend(Diesan.Diesan.getContext().getString(R.string.error_data)).isSuccess
                    }
                }
                .addOnProgressListener {
                    val progress = (100.0 * it.bytesTransferred) / it.totalByteCount
                    trySend(Class.convertDoubleToString(progress))
                }
            awaitClose {  }
        }
    }

    fun saveProduct(product: Product) {
        val data = hashMapOf(
            Constants.NAME to product.name.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            },
            Constants.IMAGE to product.image,
        )
        getInstanceFirestore().add(data)
    }

    fun getProducts(): Flow<MutableList<Product>> {
        return callbackFlow {
            val request = getInstanceFirestore()
                .orderBy(Constants.NAME, Query.Direction.ASCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE) { value, error ->
                    if (error != null) {
                        Log.w(ContentValues.TAG, "Listen failed.", error)
                        return@addSnapshotListener
                    }

                    val products = mutableListOf<Product>()
                    for (product in value!!) {
                        val productNew = Product(
                            product.id,
                            product.getString(Constants.NAME)!!,
                            image = product.getString(Constants.IMAGE)!!,
                            status = Constants.ADDED
                        )
                        products.add(productNew)
                    }
                    trySend(products)
                }
            awaitClose { request.remove() }
        }
    }

    fun updateProduct(product: Product) {
        val data: Map<String, Any> = hashMapOf(
            Constants.NAME to product.name.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            },
            Constants.IMAGE to product.image,
        )
        getInstanceFirestore()
            .document(product.id)
            .update(data)
    }

    fun deleteProducts(products: MutableList<Product>) {
        for (pro in products) {
            getInstanceFirestore().document(pro.id)
                .delete()
        }
    }
}