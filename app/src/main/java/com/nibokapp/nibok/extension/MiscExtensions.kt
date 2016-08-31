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

/**
 * Ellipsize a string.
 *
 * @param maxLength the maximum length for the string
 * @param ellipsis the string representing the ellipsis. Default = …
 *
 * @return if the string is longer than maxLength then the string is cut off at maxLength,
 * trimmed and padded with the ellipsis; otherwise the original string is returned
 */
fun String.ellipsize(maxLength: Int, ellipsis: String = "…"): String =
        if (this.length > maxLength)
            this.take(maxLength).trim() + ellipsis
        else
            this