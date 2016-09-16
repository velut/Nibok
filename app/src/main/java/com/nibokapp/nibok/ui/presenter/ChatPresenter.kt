package com.nibokapp.nibok.ui.presenter

import android.util.Log
import com.nibokapp.nibok.domain.command.chat.*
import com.nibokapp.nibok.domain.command.user.RequestLocalUserIdCommand
import com.nibokapp.nibok.domain.model.ChatMessageModel

/**
 * Presenter for the chat view.
 */
class ChatPresenter {

    companion object {
        private val TAG = ChatPresenter::class.java.simpleName
    }

    /**
     * Get the id of the local user.
     *
     * @return the id of the local user
     */
    fun getUserId() : Long {
        Log.d(TAG, "Requesting local user id")
        return RequestLocalUserIdCommand().execute()
    }

    /**
     * Get the partner's name for the conversation with the given id.
     *
     * @param conversationId the id of the conversation
     *
     * @return a String with the partner's name or null if such name was not found
     */
    fun getConversationPartnerName(conversationId: Long): String? {
        Log.d(TAG, "Requesting conversation: $conversationId partner's name")
        return RequestConversationPartnerNameCommand(conversationId).execute()
    }

    /**
     * Get the messages in the conversation with the given id.
     *
     * @param conversationId the id of the conversation
     *
     * @return the messages in the conversation
     */
    fun getConversationMessages(conversationId: Long) : List<ChatMessageModel> {
        Log.d(TAG, "Requesting messages for conversation: $conversationId")
        return RequestMessagesFromConversationCommand(conversationId).execute()
    }

    /**
     * Send a message.
     *
     * @param chatMessage the message to send
     *
     * @return true if the message was sent successfully, false otherwise
     */
    fun sendMessage(chatMessage: ChatMessageModel) : Boolean {
        Log.d(TAG, "Sending message")
        return SendMessageCommand(chatMessage).execute()
    }

    /**
     * Get the messages in the conversation that were exchanged after the given message.
     *
     * @param lastMessage the message that comes before the new messages
     *
     * @return the messages that come after the given one in the conversation
     */
    fun getNewerMessages(lastMessage: ChatMessageModel) : List<ChatMessageModel> {
        Log.d(TAG, "Requesting newer messages")
        return RequestNewerMessagesFromConversationCommand(lastMessage).execute()
    }

    /**
     * Get the messages in the conversation that were exchanged before the given message.
     *
     * @param firstMessage the message that comes before the older ones
     *
     * @return the messages that come before the given one in the conversation
     */
    fun getOlderMessages(firstMessage: ChatMessageModel) : List<ChatMessageModel> {
        Log.d(TAG, "Requesting older messages")
        return RequestOlderMessagesFromConversationCommand(firstMessage).execute()
    }
}