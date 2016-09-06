package com.nibokapp.nibok.data.db

import io.realm.RealmObject
import java.util.*

/**
 * Model class for a message belonging to a conversation.
 * Used by the local database.
 *
 * @param conversationId the id of the conversation in which the message belongs
 * @param senderId the id of the user who sent the message
 * @param text the text content of the message
 * @param date the date in which the message was sent
 */
open class Message(

        open var conversationId: Long = 0,

        open var senderId: Long = 0,

        open var text: String = "",

        open var date: Date? = null

) : RealmObject() {}
