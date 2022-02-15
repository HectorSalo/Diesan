package com.skysam.hchirinos.diesan.ui.stock

import com.skysam.hchirinos.diesan.common.dataClass.Lot

/**
 * Created by Hector Chirinos on 14/02/2022.
 */
interface StockOnClick {
 fun viewDetail(lot: Lot)
 fun sell(lot: Lot)
 fun share(lot: Lot)
}