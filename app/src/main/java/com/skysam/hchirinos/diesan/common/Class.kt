package com.skysam.hchirinos.diesan.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.skysam.hchirinos.diesan.BuildConfig

import com.skysam.hchirinos.diesan.R
import com.skysam.hchirinos.diesan.common.dataClass.Product
import java.io.FileNotFoundException
import java.text.Collator
import java.text.DateFormat
import java.util.*
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.roundToInt

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
    
    fun roundedTwoDecimals(value: Double): Double {
        var cadena = String.format(Locale.US, "%,.2f", value)
        cadena = cadena.replace(",", "")
        return cadena.toDouble()
    }
    
    fun rounded(value: Double): Double {
        return value.roundToInt().toDouble()
    }

    fun keyboardClose(view: View) {
        val imn = Diesan.Diesan.getContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imn.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun reduceBitmap(
        uri: String?,
        maxAncho: Int,
        maxAlto: Int
    ): Bitmap? {
        return try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(
                Diesan.Diesan.getContext().contentResolver.openInputStream(Uri.parse(uri)),
                null, options
            )
            options.inSampleSize = max(
                ceil(options.outWidth / maxAncho.toDouble()),
                ceil(options.outHeight / maxAlto.toDouble())
            ).toInt()
            options.inJustDecodeBounds = false
            BitmapFactory.decodeStream(
                Diesan.Diesan.getContext().contentResolver
                    .openInputStream(Uri.parse(uri)), null, options
            )
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Toast.makeText(Diesan.Diesan.getContext(), R.string.error_image_notfound, Toast.LENGTH_SHORT).show()
            null
        }
    }
    
    fun getEnviroment(): String {
        return BuildConfig.BUILD_TYPE
    }
    
    fun organizedAlphabeticList(list: MutableList<Product>): MutableList<Product> {
        Collections.sort(list, object : Comparator<Product> {
            var collator = Collator.getInstance()
            override fun compare(p0: Product?, p1: Product?): Int {
                return collator.compare(p0?.name, p1?.name)
            }
            
        })
        return list
    }
    
    fun organizedAlphabeticString(list: MutableList<String>): MutableList<String> {
        Collections.sort(list, object : Comparator<String> {
            var collator = Collator.getInstance()
            override fun compare(p0: String?, p1: String?): Int {
                return collator.compare(p0, p1)
            }
            
        })
        return list
    }
}