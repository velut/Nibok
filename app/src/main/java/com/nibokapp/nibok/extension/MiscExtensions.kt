package com.nibokapp.nibok.extension

import android.content.Context
import com.nibokapp.nibok.R
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
fun Float.toCurrency(currencyCode: String = "EUR"): String {
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
fun Date.toSimpleDateString(pattern: String = "dd/MM/yy"): String {
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(this)
}

/**
 * Describe this date based on when it happened with respect to the day before
 * the current date, two days before, one week before or any other time.
 *
 * @param yesterdayString the string to be used if this date refers to yesterday
 * @param alwaysAddHour true if the hour of the date should always be added, false otherwise. Default is false.
 *
 * @return a string that describes this date based on when it happened
 */
fun Date.toDeltaBasedSimpleDateString(yesterdayString: String, alwaysAddHour: Boolean = false):
        String {

    val calendar = Calendar.getInstance()

    calendar.add(Calendar.DATE, -1)
    val oneDayBefore = calendar.time

    calendar.add(Calendar.DATE, -1)
    val twoDaysBefore = calendar.time

    calendar.add(Calendar.DATE, -5)
    val oneWeekBefore = calendar.time

    val hourPattern = "H:mm"
    val defaultPattern: String

    if (this.after(oneDayBefore)) {
        // This date refers to today, describe it with the hour (e.g. 14:23)
        return this.toSimpleDateString(hourPattern)
    } else if (this.after(twoDaysBefore)) {
        // This date refers to yesterday,
        // describe it with the yesterday string (e.g. Yesterday)
        if (!alwaysAddHour) return yesterdayString
        return "$yesterdayString ${this.toSimpleDateString(hourPattern)}"
    } else if (this.after(oneWeekBefore)) {
        // This date belongs to the current week,
        // describe it with the day of the week (e.g. Monday)
        defaultPattern = "EEEE"
    } else {
        // This date refers to a time before the current week,
        // describe it with the full notation (e.g. 01/09/16)
        defaultPattern = "dd/MM/yy"
    }
    val pattern = if (!alwaysAddHour) defaultPattern else "$defaultPattern $hourPattern"
    return this.toSimpleDateString(pattern)
}

/**
 * Return a string with the date formatted with the given pattern-
 *
 * @param pattern the pattern to use in order to format the date. Default pattern is for RFC 1123
 *
 * @return the Date formatted into a String
 */
fun Date.toStringDate(pattern: String = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale.ENGLISH)
    return dateFormat.format(this)
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

/**
 * Parse a date from a string using the given pattern.
 *
 * @param pattern the pattern to use in order to parse the date. Default pattern is for RFC 1123
 *
 * @return the parsed Date
 */
fun String.parseDate(pattern: String = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"): Date {
    val dateFormat = SimpleDateFormat(pattern, Locale.ENGLISH)
    return dateFormat.parse(this)
}

/**
 * Safely convert a [String] to an [Int].
 *
 * @param default the value to be used if the conversion fails. Default value is 0
 *
 * @return the [Int] parsed from the [String] or the default value if NumberFormatException was raised
 */
fun String.toSafeInt(default: Int = 0): Int = try {
    this.toInt()
} catch (e: NumberFormatException) {
    default
}

/**
 * Safely convert a [String] to a [Float].
 *
 * @param default the value to be used if the conversion fails. Default value is 0f
 *
 * @return the [Float] parsed from the [String] or the default value if NumberFormatException was raised
 */
fun String.toSafeFloat(default: Float = 0f): Float = try {
    this.toFloat()
} catch (e: NumberFormatException) {
    default
}

fun String.toBookWearCondition(context: Context): String? {
    val wearConditionsArray = context.resources.getStringArray(R.array.book_wear_condition_array)
    val bookWearId = this.toSafeInt()
    return if (bookWearId >= 0 && bookWearId < wearConditionsArray.size) {
        wearConditionsArray[bookWearId]
    } else {
        null
    }
}