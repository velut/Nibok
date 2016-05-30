package com.nibokapp.nibok.domain.model

import com.nibokapp.nibok.ui.adapter.common.AdapterTypes
import com.nibokapp.nibok.ui.adapter.common.ViewType

data class BookModel(
        val title: String,
        val author: String,
        val year: Int,
        val quality: String,
        val priceIntPart: Int,
        val priceFracPart: Int,
        val thumbnail: String,
        var saved: Boolean = false
) : ViewType {
    override fun getViewType(): Int = AdapterTypes.BOOK
}

