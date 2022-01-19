package com.skysam.hchirinos.diesan.database

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.skysam.hchirinos.diesan.common.Constants
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

    fun uploadImage(uri: Uri): Flow<String> {
        return callbackFlow {
            val request = getInstanceStorage()
                .child(uri.lastPathSegment!!)
                .putFile(uri)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        offer(getInstanceStorage().child(uri.lastPathSegment!!)
                            .downloadUrl.toString())
                    } else {
                        offer("Error")
                    }
                }
        }
    }
}