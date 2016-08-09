package com.nibokapp.nibok.extension

import java.text.NumberFormat
import java.util.*

/**
 * Extension file with miscellaneous utilities.
 */

/**
 * Format a float number into a displayable currency string.
 *
 * @param currencyCode the string code representing the currency
 *
 * @return a string formatted in the current locale with the selected currency
 */
fun Float.toCurrency(currencyCode: String = "EUR") : String {
    val format = NumberFormat.getCurrencyInstance()
    format.currency = Currency.getInstance(currencyCode)
    return format.format(this)
}