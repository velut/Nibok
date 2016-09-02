package com.nibokapp.nibok.ui.adapter.delegate

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.model.MessageModel
import com.nibokapp.nibok.extension.*
import com.nibokapp.nibok.ui.adapter.common.ViewType
import com.nibokapp.nibok.ui.adapter.common.ViewTypeDelegateAdapter
import kotlinx.android.synthetic.main.item_message.view.*

/**
 * Delegate adapter managing the creation and binding of message view holders.
 */
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

        private val MAX_PARTNER_NAME_LENGTH_PORTRAIT = 15
        private val MAX_MESSAGE_CONTENT_LENGTH_PORTRAIT = 25
        private val MAX_PARTNER_NAME_LENGTH_LANDSCAPE = 25
        private val MAX_MESSAGE_CONTENT_LENGTH_LANDSCAPE = 40

        /**
         * Bind the itemView of the view holder to the given item's data.
         *
         * @param item the item containing data about the message
         */
        fun bind(item: MessageModel) {
            loadAvatar(item.partnerAvatar)
            bindData(item)
        }

        /**
         * Load the avatar of the conversation's partner into the image view.
         *
         * @param avatarSource the source of the avatar image
         */
        private fun loadAvatar(avatarSource: String) = with(itemView) {
            // TODO change placeholders
            messageAvatar.loadImg(avatarSource, animate = false)
        }

        /**
         * Bind the textual data of the message to the view.
         *
         * Shorten the message text data if necessary.
         *
         * @param item the item from which we extract the data
         */
        private fun bindData(item: MessageModel) = with(itemView) {
            // Change text views max length based on orientation
            val portrait = context.isOrientationPortrait()
            val maxPartnerNameLength =
                    if (portrait) MAX_PARTNER_NAME_LENGTH_PORTRAIT
                    else MAX_PARTNER_NAME_LENGTH_LANDSCAPE
            val maxMessageContentLength =
                    if (portrait) MAX_MESSAGE_CONTENT_LENGTH_PORTRAIT
                    else MAX_MESSAGE_CONTENT_LENGTH_LANDSCAPE

            messagePartner.text = item.partnerName.ellipsize(maxPartnerNameLength)
            messageContent.text = item.previewText.ellipsize(maxMessageContentLength)
            messageDate.text = item.date.toDeltaBasedSimpleDateString(context.getString(R.string.yesterday))
        }
    }
}
