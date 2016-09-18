package com.nibokapp.nibok.ui.filter

import android.support.design.widget.TextInputEditText
import android.text.InputFilter
import android.text.Spanned

/**
 * Collection of InputFilters for price input forms in the application.
 */


/**
 * Get an InputFilter that filters the length of price values.
 *
 * @param currentInput the TextInputEditText on which the filter operates
 * @param maxIntegerDigits the maximum number of integer digits that the price can have
 * @param maxDecimalDigits the maximum number of decimal digits that the price can have
 * @param separator the separator between integer and decimal digits
 *
 * @return an InputFilter object
 */
fun getPriceLengthFilter(currentInput: TextInputEditText, maxIntegerDigits: Int = 4,
                         maxDecimalDigits: Int = 2, separator: String = ".") =
        object : InputFilter {

            val MAX_INTEGER_DIGITS = maxIntegerDigits
            val MAX_DECIMAL_DIGITS = maxDecimalDigits
            val SEPARATOR = separator

            override fun filter(source: CharSequence, start: Int, end: Int,
                                dest: Spanned, dstart: Int, dend: Int): CharSequence? {

                val currentPrice = currentInput.text.toString() + source.toString()
                val currentPriceLen = currentPrice.length
                val separatorIndex = currentPrice.indexOf(SEPARATOR)
                var result: String? = null

                when (separatorIndex) {
                // Price input is "." and gets replaced with "0."
                    0 -> result = "0$SEPARATOR"

                // Price contains only integer digits -> limit length if necessary
                    -1 -> if (currentPriceLen > MAX_INTEGER_DIGITS) result = ""

                // Price contains separator and may contain decimal digits
                //  -> limit length if necessary
                    else -> {
                        val decimalDigitsLen = currentPrice.substring(separatorIndex + 1).length
                        if (decimalDigitsLen > MAX_DECIMAL_DIGITS) result = ""
                    }
                }

                return result
            }
        }

/**
 * Get an InputFilter that does not allow prices starting with more than one 0.
 *
 * @param currentInput the TextInputEditText on which the filter operates
 * @param separator the separator between integer and decimal digits
 *
 * @return an InputFilter object
 */
fun getPriceLeadingZerosFilter(currentInput: TextInputEditText, separator: String = ".") =
        object : InputFilter {

            val SEPARATOR = separator

            override fun filter(source: CharSequence, start: Int, end: Int,
                                dest: Spanned, dstart: Int, dend: Int): CharSequence? {

                val oldPrice = currentInput.text.toString()
                val nextChar = source.toString()
                var result: String? = null

                // A price starting with a 0 can only be in the form 0.xx, exclude prices where 0
                // is the leading digit and more digits follow before the separator (e.g. 0123.45)
                if (oldPrice == "0" && nextChar != SEPARATOR) {
                    result = ""
                }

                return result
            }
        }