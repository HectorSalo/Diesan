package com.skysam.hchirinos.diesan.ui.stats

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.diesan.common.dataClass.Sale
import com.skysam.hchirinos.diesan.database.SaleRespository

/**
 * Created by Hector Chirinos on 21/02/2022.
 */

class StatsViewModel: ViewModel() {
	val sales: LiveData<MutableList<Sale>> = SaleRespository.getSales().asLiveData()
}