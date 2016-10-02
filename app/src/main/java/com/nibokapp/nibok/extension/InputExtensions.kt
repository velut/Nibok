package com.nibokapp.nibok.extension

import android.support.design.widget.TextInputEditText
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