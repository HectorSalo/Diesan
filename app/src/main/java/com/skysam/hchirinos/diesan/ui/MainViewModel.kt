package com.skysam.hchirinos.diesan.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skysam.hchirinos.diesan.database.ProductRepository

class MainViewModel : ViewModel() {
    private val _urlDownload = MutableLiveData<String>()
    val urlDownload: LiveData<String> get() = _urlDownload

    fun uploadImage(uri: Uri): LiveData<String> {
        return ProductRepository.uploadImage(uri).asLiveData()
    }
}