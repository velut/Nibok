package com.nibokapp.nibok.ui.adapter.delegate

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.model.MessageModel
import com.nibokapp.nibok.extension.inflate
import com.nibokapp.nibok.ui.adapter.common.ViewType
import com.nibokapp.nibok.ui.adapter.common.ViewTypeDelegateAdapter

class MessageDelegateAdapter : ViewTypeDelegateAdapter {

    companion object {
        private val TAG = MessageDelegateAdapter::class.java.simpleName
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return MessageVH(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType) {
        holder as MessageVH
        holder.bind(item as MessageModel)
    }

    class MessageVH(parent: ViewGroup) :
            RecyclerView.ViewHolder(parent.inflate(R.layout.item_message)) {

        fun bind(item: MessageModel) {
            // TODO bind data
        }
    }
}
