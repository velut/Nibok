package com.nibokapp.nibok.ui.adapter.viewtype.common

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

/**
 * Interface common to all delegate adapters.
 *
 * It defines the common behaviors of creating and binding view holders.
 */

interface ViewTypeDelegateAdapter {

    fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder

    fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType)
}