package com.nibokapp.nibok.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.model.ChatMessage
import com.nibokapp.nibok.extension.inflate
import com.nibokapp.nibok.extension.toDeltaBasedSimpleDateString
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

        /**
         * Get the text content of the message.
         *
         * @param message the message containing the text
         *
         * @return the text of the message
         */
        fun getMessageText(message: ChatMessage) = message.text

        /**
         * Get the date of the message.
         *
         * @param message the message containing the date
         * @param context the context used to retrieve string resources
         *
         * @return the a string representing the time at which the message was sent
         */
        fun getMessageDate(message: ChatMessage, context: Context) =
                message.date.toDeltaBasedSimpleDateString(
                        context.resources.getString(R.string.yesterday), alwaysAddHour = true)
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
     *
     * @return the position in which the last message was added
     */
    fun addMessages(messageList: List<ChatMessage>) : Int {
        var position: Int = 0
        messageList.forEach { position = addMessage(it) }
        return position
    }

    /**
     * Add a message to the current message list.
     * The message is added to the bottom of the current list.
     *
     * @param message the message to add
     *
     * @return the position in which the message was added
     */
    fun addMessage(message: ChatMessage) : Int {
        messages.add(message)
        val position = itemCount - 1
        notifyItemInserted(position)
        return position
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
            rightBubbleContent.text = getMessageText(message)
        }

        private fun bindDate(message: ChatMessage) = with(itemView) {
            rightBubbleDate.text = getMessageDate(message, context)
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
            leftBubbleContent.text = getMessageText(message)
        }

        private fun bindDate(message: ChatMessage) = with(itemView) {
            leftBubbleDate.text = getMessageDate(message, context)
        }
    }
}
