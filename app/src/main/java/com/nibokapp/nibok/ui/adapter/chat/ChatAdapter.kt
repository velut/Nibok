package com.nibokapp.nibok.ui.adapter.chat

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.model.ChatMessageModel
import com.nibokapp.nibok.extension.inflate
import com.nibokapp.nibok.extension.toDeltaBasedSimpleDateString
import org.jetbrains.anko.find

/**
 * Adapter for chat messages.
 *
 * It manages the messages exchanged between the user and the conversation's partner
 * and displays them in the chat recycler view.
 *
 * @param userId the id of the local user
 */
class ChatAdapter(val userId: String) : RecyclerView.Adapter<ChatAdapter.ChatMessageVH>() {

    companion object {
        /**
         * View types for user message and partner message.
         */
        private const val USER_MESSAGE = 0
        private const val PARTNER_MESSAGE = 1

        /**
         * Layouts for user message and partner message.
         */
        private val USER_MSG_LAYOUT = R.layout.message_chat_bubble_right
        private val PARTNER_MSG_LAYOUT = R.layout.message_chat_bubble_left
    }

    /**
     * List of messages exchanged by the user and the conversation's partner.
     */
    private val messages = mutableListOf<ChatMessageModel>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageVH =
            if (viewType == USER_MESSAGE)
                ChatMessageVH(parent.inflate(USER_MSG_LAYOUT))
            else
                ChatMessageVH(parent.inflate(PARTNER_MSG_LAYOUT))

    override fun onBindViewHolder(holder: ChatMessageVH, position: Int) {
        val message = messages[position]
        holder.bind(message)
    }

    override fun getItemViewType(position: Int): Int =
            if (messages[position].senderId == userId)
                USER_MESSAGE
            else
                PARTNER_MESSAGE

    override fun getItemCount(): Int = messages.size

    /**
     * Add a list of messages to the current message list.
     * Messages are always added to the bottom of the current list.
     *
     * @param messageList the list of messages to add
     *
     * @return the position in which the last message was added or null if it was not added
     */
    fun addMessages(messageList: List<ChatMessageModel>) : Int? {
        var position: Int? = null
        messageList.forEach { position = addMessage(it) }
        return position
    }

    /**
     * Add a message to the current message list.
     * The message is added to the bottom of the current list.
     *
     * @param message the message to add
     *
     * @return the position in which the message was added or null if it was not added
     */
    fun addMessage(message: ChatMessageModel) : Int? {

        if (message in messages) return null

        messages.add(message)
        val position = itemCount - 1
        notifyItemInserted(position)
        return position
    }

    /**
     * Return the first message available in the list of messages.
     *
     * @return the first message available in the list of messages if it exists,
     * null otherwise
     */
    fun getFirstMessage() : ChatMessageModel? = messages.firstOrNull()

    /**
     * Return the last message available in the list of messages.
     *
     * @return the last message available in the list of messages if it exists,
     * null otherwise
     */
    fun getLastMessage() : ChatMessageModel? = messages.lastOrNull()

    /**
     * ViewHolder for chat messages.
     */
    class ChatMessageVH(itemView: View): RecyclerView.ViewHolder(itemView) {

        /**
         * Bind the data from the message into the view.
         *
         * @param message the message object containing the data to be bound
         */
        fun bind(message: ChatMessageModel) = with(itemView) {
            with(message) {
                val contentView: TextView = find(R.id.msgTextContent)
                val dateView: TextView = find(R.id.msgDate)
                val dateText = date.toDeltaBasedSimpleDateString(
                        context.resources.getString(R.string.yesterday), true)

                contentView.text = text
                dateView.text = dateText
            }
        }
    }
}
