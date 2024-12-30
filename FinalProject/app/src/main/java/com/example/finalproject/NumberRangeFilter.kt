// com.example.finalproject.utils.NumberRangeFilter.kt
package com.example.finalproject.utils

import android.text.InputFilter
import android.text.Spanned

class NumberRangeFilter(private val min: Int, private val max: Int) : InputFilter {
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        try {
            val input = (dest?.subSequence(0, dstart).toString() +
                    source?.subSequence(start, end) +
                    dest?.subSequence(dend, dest.length)).toInt()
            if (input in min..max) {
                return null // Geçerli giriş
            }
        } catch (e: NumberFormatException) {
            // Sayıya dönüştürülemeyen girişler burada yakalanır
        }
        return "" // Geçersiz girişleri engelle
    }
}
