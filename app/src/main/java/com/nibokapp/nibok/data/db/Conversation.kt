package com.nibokapp.nibok.data.db

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Model class for a conversation between the local user and an external user (partner).
 * Used by the local database.
 *
 * @param id the id of the conversation
 * @param userId the id of the local user
 * @param partner the external user participating in the conversation
 * @param date the date in which the conversation was started
 * @param messages the list of messages exchanged in the conversation - One to many
 */
open class Conversation(

        @PrimaryKey open var id: String = "",

        open var userId: String = "",

        open var partner: ExternalUser? = null,

        open var date: Date? = null,

        open var messages: RealmList<Message> = RealmList()

) : RealmObject() {}
