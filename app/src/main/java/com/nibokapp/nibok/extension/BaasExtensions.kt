package com.nibokapp.nibok.extension

import com.baasbox.android.BaasResult

/**
 * Extensions handling Baas related operations.
 */

/**
 * Given a BaasResult on a type T if the result is successful execute the given
 * function on T.
 *
 * @param func a function that operates on the data of type T contained in the result
 *
 * @return true if the result was successful and 'func' was called,
 * false if the result was not successful and 'func' was not called
 */
inline fun <reified T> BaasResult<T>.onSuccess(func: (T) -> Unit) : Boolean {
    if (this.isSuccess && this.value() != null) {
        func(this.value())
        return true
    }
    return false
}