package com.nibokapp.nibok.data.db

import com.nibokapp.nibok.data.db.common.WellFormedItem
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Model class for a message belonging to a conversation.
 * Used by the local database.
 *
 * @param id the id of the message
 * @param conversationId the id of the conversation in which the message belongs
 * @param senderId the id of the user who sent the message
 * @param text the text content of the message
 * @param date the date in which the message was sent
 */
open class Message(

        @PrimaryKey
        open var id: String = "",

        open var conversationId: String = "",

        open var senderId: String = "",

        open var text: String = "",

        open var date: Date? = null

) : RealmObject(), WellFormedItem {

    override fun isWellFormed(): Boolean = with(this) { date != null }
}
