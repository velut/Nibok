package com.nibokapp.nibok.extension

import org.jetbrains.anko.doAsync

/**
 * Extensions for the base repository
 */

/**
 * Given a list of sources return the result from the first source that provides it.
 *
 * @param predicate the way in which to retrieve the result
 *
 * @return a Pair containing the result and the source that generated it
 *         or a Pair of null, null if no result was found
 */
inline fun <T, R : Any> Iterable<T>.firstResultOrNull(predicate: (T) -> R?): Pair<R?, T?> {
    for (element in this) {
        val result = predicate(element)
        if (result != null) return Pair(result, element)
    }
    return Pair(null, null)
}

/**
 * Given a list of sources return the result from the first source that provides it.
 * Results that are empty lists are discarded.
 *
 * @param predicate the way in which to retrieve the result
 *
 * @return a Pair containing the result (non empty list) and the source that generated it
 *         or a Pair of null, null if no result was found
 */
@Suppress("LoopToCallChain")
inline fun <T, R : Any> Iterable<T>.firstListResultOrNull(predicate: (T) -> List<R>?): Pair<List<R>?, T?> {
    for (element in this) {
        val result = predicate(element)
        if (result != null && result.isNotEmpty()) return Pair(result, element)
    }
    return Pair(null, null)
}

/**
 * Given a Pair<Result, Source> try to store the result through onStore() and return the result.
 *
 * @param onStore the function used to store the result,
 *                it takes as arguments the item to store and the source that generated it
 *
 * @return the result
 */
fun <T, R : Any> Pair<R?, T?>.storeAndReturnResult(onStore: (R?, T?) -> Unit): R? {
    val result = this.first
    val source = this.second
    doAsync { onStore(result, source) }
    return result
}

/**
 * Given a Pair<ResultList, Source> try to store the result list through onStore() and return the result list.
 *
 * @param onStore the function used to store the result list,
 *                it takes as arguments the list of items to store and the source that generated it
 *
 * @return the result list
 */
fun <T, R : Any> Pair<List<R>?, T?>.storeAndReturnListResult(onStore: (List<R>?, T?) -> Unit): List<R>? {
    val resultList = this.first
    val source = this.second
    doAsync { onStore(resultList, source) }
    return resultList
}

/**
 * First query the sources and then try to store the result.
 */
inline fun <T, R : Any> Iterable<T>.firstListResultOrNullWithStorage(
        predicate: (T) -> List<R>?,
        crossinline onStore: (List<R>?, T?) -> Unit
): List<R>? {
    return this.firstListResultOrNull { predicate(it) }
            .storeAndReturnListResult { list, source -> onStore(list, source) }
}