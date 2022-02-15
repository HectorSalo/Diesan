package com.skysam.hchirinos.diesan.ui.stock

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.diesan.common.dataClass.Lot
import com.skysam.hchirinos.diesan.database.StockRepository

/**
 * Created by Hector Chirinos on 14/02/2022.
 */
class StockViewModel: ViewModel() {
 val lots: LiveData<MutableList<Lot>> = StockRepository.getLotsFromStock().asLiveData()

 private val _lotToView = MutableLiveData<Lot>()
 val lotToView: LiveData<Lot> get() = _lotToView

 fun viewLot(lot: Lot) {
  _lotToView.value = lot
 }
}