package com.skysam.hchirinos.diesan.common

import java.util.*

/**
 * Created by Hector Chirinos on 06/01/2022.
 */
object Class {
    fun convertDoubleToString(value: Double): String {
        return String.format(Locale.GERMANY, "%,.2f", value)
    }

    fun convertIntToString(value: Int): String {
        return String.format(Locale.GERMANY, "%,.2f", value.toDouble())
    }
}