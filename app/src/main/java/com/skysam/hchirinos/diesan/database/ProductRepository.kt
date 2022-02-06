package com.skysam.hchirinos.diesan.database

import android.net.Uri
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
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
                        /*trySend(
                            getInstanceStorage().child(uri.lastPathSegment!!)
                                .downloadUrl.toString()
                        ).isSuccess*/
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
            Constants.NAME to product.name,
            Constants.IMAGE to product.image,
        )
        getInstanceFirestore().add(data)
    }

    fun getProducts(): Flow<Product?> {
        return callbackFlow {
            val request = getInstanceFirestore()
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }

                    if (value!!.documentChanges.isEmpty()) {
                        trySend(null)
                        return@addSnapshotListener
                    }

                    for (doc in value.documentChanges) {
                        when (doc.type) {
                            DocumentChange.Type.ADDED -> {
                                val product = Product(
                                    doc.document.id,
                                    doc.document.getString(Constants.NAME)!!,
                                    image = doc.document.getString(Constants.IMAGE)!!,
                                    status = Constants.ADDED
                                )
                                trySend(product)
                            }
                            DocumentChange.Type.MODIFIED -> {
                                val product = Product(
                                    doc.document.id,
                                    doc.document.getString(Constants.NAME)!!,
                                    image = doc.document.getString(Constants.IMAGE)!!,
                                    status = Constants.MODIFIED
                                )
                                trySend(product)
                            }
                            DocumentChange.Type.REMOVED -> {
                                val product = Product(
                                    doc.document.id,
                                    doc.document.getString(Constants.NAME)!!,
                                    image = doc.document.getString(Constants.IMAGE)!!,
                                    status = Constants.REMOVED
                                )
                                trySend(product)
                            }
                        }
                    }
                }
            awaitClose { request.remove() }
        }
    }
}