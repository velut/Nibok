package com.nibokapp.nibok.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.nibokapp.nibok.domain.model.BookModel
import com.nibokapp.nibok.ui.adapter.common.AdapterTypes
import com.nibokapp.nibok.ui.adapter.common.ViewType
import com.nibokapp.nibok.ui.adapter.delegate.BookDelegateAdapter
import com.nibokapp.nibok.ui.adapter.delegate.LoadingDelegateAdapter


class BookAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val loadingItem = object : ViewType {
        override fun getViewType(): Int = AdapterTypes.LOADING
    }

    private val items = mutableListOf<ViewType>(loadingItem)

    private val delegateAdaptersMap = mapOf(
            AdapterTypes.LOADING to LoadingDelegateAdapter(),
            AdapterTypes.BOOK to BookDelegateAdapter()
    )


    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int = items[position].getViewType()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return delegateAdaptersMap[viewType]!!.onCreateViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        return delegateAdaptersMap[getItemViewType(position)]!!.onBindViewHolder(holder, items[position])
    }

    fun addBooks(books: List<BookModel>, positionStart: Int = 0) {
        val insertItemCount = books.size
        items.addAll(positionStart, books)
        notifyItemRangeInserted(positionStart, insertItemCount)
    }

}