package com.nibokapp.nibok.ui.filter

import android.text.InputFilter

/**
 * Collection of InputFilters for price input forms in the application.
 */


/**
 * Get an InputFilter that allows only alphanumeric characters.
 *
 * @return an InputFilter object only allowing alphanumeric characters
 */
fun getAlphanumericFilter() =
        InputFilter { source, start, end, dest, dstart, dend ->
            val sourceIsAllowed = with(source) {
                matches(Regex("[0-9]")) ||
                        matches(Regex("[a-z]")) ||
                        matches(Regex("[A-Z]"))
            }
            if (sourceIsAllowed) source else ""
        }