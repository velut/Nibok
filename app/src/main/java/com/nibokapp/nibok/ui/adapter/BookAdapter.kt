package com.nibokapp.nibok.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.nibokapp.nibok.ui.adapter.common.AdapterConstants
import com.nibokapp.nibok.ui.adapter.common.ViewType
import com.nibokapp.nibok.ui.adapter.common.ViewTypeDelegateAdapter
import com.nibokapp.nibok.ui.adapter.delegate.LoadingDelegateAdapter


class BookAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val loadingItem = object : ViewType {
        override fun getViewType(): Int = AdapterConstants.LOADING
    }

    private val items = mutableListOf(loadingItem)

    private val delegateAdaptersMap = mapOf<Int, ViewTypeDelegateAdapter>(
            AdapterConstants.LOADING to LoadingDelegateAdapter()
    )


    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int = items[position].getViewType()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return delegateAdaptersMap[viewType]!!.onCreateViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        return delegateAdaptersMap[getItemViewType(position)]!!.onBindViewHolder(holder, items[position])
    }
}