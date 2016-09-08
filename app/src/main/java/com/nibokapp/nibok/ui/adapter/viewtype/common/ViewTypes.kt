package com.nibokapp.nibok.ui.adapter.viewtype.common

/**
 * Enumeration of view types.
 */
object ViewTypes {

    val LOADING = 1
    val BOOK = 2
    val MESSAGE = 3

    private val typeNames = mapOf(
            LOADING to "Loading",
            BOOK to "Book",
            MESSAGE to "Message"
    )

    /**
     * Get the name associated to a given type.
     *
     * @param type the type of which the name is needed
     *
     * @return the name of the type or "Wrong type" if no such type is present.
     */
    fun getTypeName(type: Int) = typeNames[type] ?: "Wrong type"
}