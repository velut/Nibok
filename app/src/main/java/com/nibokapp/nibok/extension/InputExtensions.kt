package com.nibokapp.nibok.extension

import android.support.design.widget.TextInputEditText
import android.support.v7.widget.SearchView
import android.text.Editable
import android.text.TextWatcher

/**
 * Extensions for Android inputs.
 */

/**
 * Execute the given function when the text of this TextInputEditText changes.
 *
 * @param func the function to execute on text change
 */
inline fun TextInputEditText.afterTextChanged(crossinline func: () -> Unit) = with(this) {
    addTextChangedListener(
            object : TextWatcher {

                override fun afterTextChanged(s: Editable?) {
                    func()
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            }
    )
}

/**
 * Listen for query text change and submit on this SearchView.
 *
 * @param onQueryTextSubmit the function handling the query on its submission
 * @param onQueryTextChange the function handling the changing query text
 */
inline fun SearchView.onQueryListener(crossinline onQueryTextSubmit: (String) -> Boolean,
                                      crossinline onQueryTextChange: (String) -> Boolean) = with(this) {
    setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return query?.let { onQueryTextSubmit(it) } ?: false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            return newText?.let { onQueryTextChange(it) } ?: false
        }
    })
}