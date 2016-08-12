package com.nibokapp.nibok.extension

import java.text.NumberFormat
import java.text.SimpleDateFormat
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

/**
 * Format a date into a string with the given pattern.
 *
 * @param pattern the formatting pattern for the date
 *
 * @return a string representing the date formatted with the pattern
 */
fun Date.toSimpleDateString(pattern: String = "dd/MM/yy") : String {
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(this)
}