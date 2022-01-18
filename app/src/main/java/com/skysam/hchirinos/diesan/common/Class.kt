package com.skysam.hchirinos.diesan.common

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.text.DateFormat
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

    fun convertDateToString(value: Date): String {
        return DateFormat.getDateInstance().format(value)
    }

    fun keyboardClose(view: View) {
        val imn = Diesan.Diesan.getContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imn.hideSoftInputFromWindow(view.windowToken, 0)
    }
}