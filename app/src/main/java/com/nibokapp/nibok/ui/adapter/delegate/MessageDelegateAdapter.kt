package com.nibokapp.nibok.ui.adapter.delegate

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.model.MessageModel
import com.nibokapp.nibok.extension.ellipsize
import com.nibokapp.nibok.extension.inflate
import com.nibokapp.nibok.extension.toDeltaBasedSimpleDateString
import com.nibokapp.nibok.ui.adapter.common.ViewType
import com.nibokapp.nibok.ui.adapter.common.ViewTypeDelegateAdapter
import kotlinx.android.synthetic.main.item_message.view.*

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

        private val MAX_PARTNER_NAME_LENGTH = 15
        private val MAX_MESSAGE_CONTENT_LENGTH = 25

        fun bind(item: MessageModel) {
            bindData(item)
        }

        private fun bindData(item: MessageModel) = with(itemView) {
            messagePartner.text = item.partnerName.ellipsize(MAX_PARTNER_NAME_LENGTH)
            messageContent.text = item.previewText.ellipsize(MAX_MESSAGE_CONTENT_LENGTH)
            messageDate.text = item.date.toDeltaBasedSimpleDateString(context.getString(R.string.yesterday))
        }
    }
}
