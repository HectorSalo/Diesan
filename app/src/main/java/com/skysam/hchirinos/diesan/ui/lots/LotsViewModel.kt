package com.skysam.hchirinos.diesan.ui.lots

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LotsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is gallery Fragment"
    }
    val text: LiveData<String> = _text

    private val _total = MutableLiveData<Double>().apply {
        value = 0.0
    }
    val total: LiveData<Double> = _total

    fun addTotal(value: Double) {
        _total.value = _total.value!! + value
    }

    fun restTotal(value: Double) {
        _total.value = _total.value!! - value
    }
}