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

    fun addBooks(books: List<BookModel>, addToTop: Boolean = true) {
        val insertPosition = if (addToTop) 0 else itemCount - 1 // at top or at loading item position
        val insertItemCount = books.size

        items.addAll(insertPosition, books)
        notifyItemRangeInserted(insertPosition, insertItemCount)
    }

    fun removeLoadingItem() {
        if (items.contains(loadingItem)) {
            val loadingItemPosition = itemCount - 1
            items.remove(loadingItem)
            notifyItemRemoved(loadingItemPosition)
        }
    }

}