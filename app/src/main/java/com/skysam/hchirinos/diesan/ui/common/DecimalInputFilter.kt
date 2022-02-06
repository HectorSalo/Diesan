package com.skysam.hchirinos.diesan.ui.common

import android.text.InputFilter
import android.text.Spanned
import java.util.regex.Pattern

/**
 * Created by Hector Chirinos on 08/01/2022.
 */
class DecimalInputFilter(digitsBeforeZero: Int, digitsAfterZero: Int): InputFilter {
    var mPattern: Pattern? = null

    init {
        if (mPattern == null) {
            mPattern =
                Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+" +
                        "((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?")
        }
    }

    override fun filter(
        p0: CharSequence?,
        p1: Int,
        p2: Int,
        p3: Spanned?,
        p4: Int,
        p5: Int
    ): CharSequence? {
        val matcher = mPattern!!.matcher(p3)
        return if (!matcher.matches()) "" else null
    }
}