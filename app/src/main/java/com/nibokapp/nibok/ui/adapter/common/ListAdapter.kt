package com.nibokapp.nibok.ui.adapter.common

interface ListAdapter<in T> {

    fun addItems(items: List<T>)

    fun clearAndAddItems(items: List<T>)

}
