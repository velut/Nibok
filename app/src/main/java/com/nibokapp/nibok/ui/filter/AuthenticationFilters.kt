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
            val sourceIsAllowed = source.matches(Regex("^[a-zA-Z0-9]+$"))
            if (sourceIsAllowed) source else ""
        }