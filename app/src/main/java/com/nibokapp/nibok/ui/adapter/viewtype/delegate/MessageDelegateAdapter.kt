package com.nibokapp.nibok.ui.adapter.viewtype.delegate

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.ViewGroup
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.model.ConversationModel
import com.nibokapp.nibok.extension.*
import com.nibokapp.nibok.ui.adapter.viewtype.ViewTypeAdapter
import com.nibokapp.nibok.ui.adapter.viewtype.common.ViewType
import com.nibokapp.nibok.ui.adapter.viewtype.common.ViewTypeDelegateAdapter
import com.nibokapp.nibok.ui.adapter.viewtype.common.ViewTypes
import kotlinx.android.synthetic.main.item_message.view.*

/**
 * Delegate adapter managing the creation and binding of message view holders.
 */
class MessageDelegateAdapter(val itemClickManager: ViewTypeAdapter.ItemClickManager) : ViewTypeDelegateAdapter {

    companion object {
        private val TAG = MessageDelegateAdapter::class.java.simpleName
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return MessageVH(parent, itemClickManager)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType) {
        holder as MessageVH
        holder.bind(item as ConversationModel)
    }

    class MessageVH(parent: ViewGroup, val itemClickManager: ViewTypeAdapter.ItemClickManager) :
            RecyclerView.ViewHolder(parent.inflate(R.layout.item_message)) {

        private val MAX_PARTNER_NAME_LENGTH_PORTRAIT = 15
        private val MAX_MESSAGE_CONTENT_LENGTH_PORTRAIT = 25
        private val MAX_PARTNER_NAME_LENGTH_LANDSCAPE = 25
        private val MAX_MESSAGE_CONTENT_LENGTH_LANDSCAPE = 40

        private var conversationId: String? = null

        /**
         * Bind the itemView of the view holder to the given item's data.
         *
         * @param item the item containing data about the message
         */
        fun bind(item: ConversationModel) {
            conversationId = item.conversationId
            loadAvatar(item.partner.avatar)
            bindData(item)
            addClickListener()
        }

        /**
         * Load the avatar of the conversation's partner into the image view.
         *
         * @param avatarSource the source of the avatar image
         */
        private fun loadAvatar(avatarSource: String) = with(itemView) {
            val placeholder = R.drawable.ic_account_circle_dark_green_48dp
            messageAvatar.loadImg(avatarSource, placeholder, placeholder, animate = false)
        }

        /**
         * Bind the textual data of the message to the view.
         *
         * Shorten the message text data if necessary.
         *
         * @param item the item from which we extract the data
         */
        private fun bindData(item: ConversationModel) = with(itemView) {
            // Change text views max length based on orientation
            val portrait = context.isOrientationPortrait()
            val maxPartnerNameLength =
                    if (portrait) MAX_PARTNER_NAME_LENGTH_PORTRAIT
                    else MAX_PARTNER_NAME_LENGTH_LANDSCAPE
            val maxMessageContentLength =
                    if (portrait) MAX_MESSAGE_CONTENT_LENGTH_PORTRAIT
                    else MAX_MESSAGE_CONTENT_LENGTH_LANDSCAPE

            with(item) {
                messagePartner.text = partner.username.ellipsize(maxPartnerNameLength)
                messageContent.text = previewText.ellipsize(maxMessageContentLength)
                messageDate.text = date.toDeltaBasedSimpleDateString(context.getString(R.string.yesterday))
            }

        }

        /**
         * Add a click listener to the layout's root.
         */
        private fun addClickListener() = with(itemView) {
            setOnClickListener {
                Log.d(TAG, "Message item clicked")
                conversationId?.let {
                    itemClickManager.onItemClick(it, ViewTypes.MESSAGE)
                }
            }
        }
    }
}
