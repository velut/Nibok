package com.nibokapp.nibok.server.provider.common

import com.baasbox.android.json.JsonArray
import com.nibokapp.nibok.data.db.Conversation
import com.nibokapp.nibok.data.db.Insertion

/**
 * ServerDataProviderInterface.
 * This is an interface for objects that provide data from the server.
 */
interface ServerDataProviderInterface {

    /**
     * Get the list of Insertion corresponding to the ids contained in the given array
     *
     * @param idsArray the array of ids of the insertions
     *
     * @return a list of Insertion
     */
    fun getInsertionListFromIds(idsArray: JsonArray) : List<Insertion>

    /**
     * Get the list of Conversation corresponding to the ids contained in the given array
     *
     * @param idsArray the array of ids of the conversations
     *
     * @return a list of Conversation
     */
    fun getConversationListFromIds(idsArray: JsonArray): List<Conversation>
}
