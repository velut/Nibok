package com.nibokapp.nibok.data.db

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Model class for a conversation between the local user and an external user (partner).
 * Used by the local database.
 *
 * @param id the id of the conversation
 * @param partner the external user participating in the conversation
 * @param messages the list of messages exchanged in the conversation - One to many
 */
open class Conversation(

        @PrimaryKey open var id: Long = 0,

        open var partner: ExternalUser? = null,

        open var messages: RealmList<Message> = RealmList()

) : RealmObject() {}
