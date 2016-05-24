package com.nibokapp.nibok.ui.adapter.common

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

/**
 * Created by edo on 24/05/2016.
 */

interface ViewTypeDelegateAdapter {

    fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder

    fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType)
}