package com.nibokapp.nibok.ui.adapter.viewtype.delegate

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.nibokapp.nibok.R
import com.nibokapp.nibok.extension.inflate
import com.nibokapp.nibok.ui.adapter.viewtype.common.ViewType
import com.nibokapp.nibok.ui.adapter.viewtype.common.ViewTypeDelegateAdapter

/**
 * Delegated adapter that displays the loading item (an animated icon).
 */
class LoadingDelegateAdapter : ViewTypeDelegateAdapter {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = LoadingVH(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType) {
    }

    class LoadingVH(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.loading_item))
}