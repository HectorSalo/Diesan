package com.skysam.hchirinos.diesan.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.skysam.hchirinos.diesan.R
import java.io.FileNotFoundException
import java.text.DateFormat
import java.util.*
import kotlin.math.ceil
import kotlin.math.max

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
}