package com.nibokapp.nibok.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.model.ChatMessage
import com.nibokapp.nibok.extension.inflate
import com.nibokapp.nibok.extension.toSimpleDateString
import kotlinx.android.synthetic.main.message_chat_bubble_left.view.*
import kotlinx.android.synthetic.main.message_chat_bubble_right.view.*

/**
 * Adapter for chat messages.
 *
 * It manages the messages exchanged between the user and the conversation's partner
 * and displays them in the chat recycler view.
 *
 * @param userId the id of the local user
 */
class ChatAdapter(val userId: Long) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        /**
         * View types for user message and partner message.
         */
        val USER_MESSAGE = 0
        val PARTNER_MESSAGE = 1
    }

    /**
     * Interface describing what a chat message view holder should do.
     */
    interface ChatMessageVH {

        /**
         * Bind the data from the message into the view.
         *
         * @param message the message object containing the data to be bound
         */
        fun bind(message: ChatMessage)
    }

    /**
     * List of messages exchanged by the user and the conversation's partner.
     */
    private val messages = mutableListOf<ChatMessage>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == USER_MESSAGE) {
            return UserMessageVH(parent)
        } else {
            return PartnerMessageVH(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        (holder as ChatMessageVH).bind(message)
    }

    override fun getItemViewType(position: Int): Int {
        if (messages[position].senderId == userId) {
            return USER_MESSAGE
        } else {
            return PARTNER_MESSAGE
        }
    }

    override fun getItemCount(): Int = messages.size

    /**
     * Add a list of messages to the current message list.
     * Messages are always added to the bottom of the current list.
     *
     * @param messageList the list of messages to add
     */
    fun addMessages(messageList: List<ChatMessage>) = messageList.forEach { addMessage(it) }

    /**
     * Add a message to the current message list.
     * The message is added to the bottom of the current list.
     *
     * @param message the message to add
     */
    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(itemCount - 1)
    }

    /**
     * ViewHolder for messages sent by the user.
     */
    private class UserMessageVH(parent: ViewGroup) :
            RecyclerView.ViewHolder(parent.inflate(R.layout.message_chat_bubble_right)),
            ChatMessageVH {

        override fun bind(message: ChatMessage) {
            bindText(message)
            bindDate(message)
        }

        private fun bindText(message: ChatMessage) = with(itemView) {
            rightBubbleContent.text = message.text
        }

        private fun bindDate(message: ChatMessage) = with(itemView) {
            rightBubbleDate.text = message.date.toSimpleDateString()
        }
    }

    /**
     * ViewHolder for messages sent by the conversation's partner and received by the user.
     */
    private class PartnerMessageVH(parent: ViewGroup) :
            RecyclerView.ViewHolder(parent.inflate(R.layout.message_chat_bubble_left)),
            ChatMessageVH {

        override fun bind(message: ChatMessage) {
            bindText(message)
            bindDate(message)
        }

        private fun bindText(message: ChatMessage) = with(itemView) {
            leftBubbleContent.text = message.text
        }

        private fun bindDate(message: ChatMessage) = with(itemView) {
            leftBubbleDate.text = message.date.toSimpleDateString()
        }
    }
}
