package com.nibokapp.nibok.ui.presenter

import com.nibokapp.nibok.data.repository.UserManager
import com.nibokapp.nibok.domain.model.ChatMessageModel
import java.util.*

/**
 * Presenter for the chat view.
 */
class ChatPresenter {

    /**
     * Get the id of the local user.
     *
     * @return the id of the local user
     */
    fun getUserId() : Long = UserManager.getUserId()

    /**
     * TODO
     * Get the messages in the conversation with the given id.
     *
     * @param conversationId the id of the conversation
     *
     * @return the messages in the conversation
     */
    fun getConversationMessages(conversationId: Long) : List<ChatMessageModel> =
            (1..10).map {
                val date = Calendar.getInstance()
                date.add(Calendar.DATE, -10 + it)
                val content = arrayOf("Lorem Ipsum", "Lorem Ipsum Lorem Ipsum Lorem Ipsum")
                val user = it % 2
                ChatMessageModel(conversationId, user.toLong(), content[user], date.time)
            }

    /**
     * TODO
     * Send a message.
     *
     * @param chatMessage the message to send
     *
     * @return true if the message was sent successfully, false otherwise
     */
    fun sendMessage(chatMessage: ChatMessageModel) : Boolean = true

    /**
     * TODO
     * Get the messages in the conversation with the given id
     * that were exchanged after the given message.
     *
     * @param conversationId the id of the conversation
     * @param afterMessage the message that comes before the new messages
     *
     * @return the messages that come after the given message in the conversation
     */
    fun getNewMessages(conversationId: Long, afterMessage: ChatMessageModel) :
            List<ChatMessageModel> =
            (1..3).map {
                val date = Calendar.getInstance()
                date.add(Calendar.DATE, -10 + it)
                val content = arrayOf("Lorem Ipsum", "Lorem Ipsum Lorem Ipsum Lorem Ipsum")
                val user = 0
                ChatMessageModel(conversationId, user.toLong(), content[user], date.time)
            }
}