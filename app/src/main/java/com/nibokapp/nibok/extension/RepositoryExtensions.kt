package com.nibokapp.nibok.extension

/**
 * Extensions for the base repository
 */

/**
 * Given a list of sources return the result from the first source that provides it.
 *
 * @param predicate the way in which to retrieve the result
 *
 * @return a result or null if no result was found
 */
inline fun <T, R: Any> Iterable<T>.firstResultOrNull(predicate: (T) -> R?) : R? {
    for (element in this) {
        val result = predicate(element)
        if (result != null) return result
    }
    return null
}