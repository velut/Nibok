package com.nibokapp.nibok.server.send.common

import android.net.Uri
import com.baasbox.android.BaasDocument

interface ServerDataSenderInterface {

    /**
     * Send the pictures taken for an insertion to the server.
     *
     * @param fileUris the list of Uri associated to the pictures
     *
     * @return a List of Strings representing pictures' Uris if pictures were sent successfully,
     *         null otherwise
     */
    fun sendInsertionPictures(fileUris: List<Uri>): List<String>?

    /**
     * Send a BaasDocument representing book data to the server.
     *
     * @param document the document to send
     *
     * @return the String representing the id of the document if the document was sent successfully,
     *         null otherwise
     */
    fun sendBookDocument(document: BaasDocument): String?

    /**
     * Send a BaasDocument representing insertion data to the server.
     *
     * @param document the document to send
     *
     * @return the String representing the id of the document if the document was sent successfully,
     *         null otherwise
     */
    fun sendInsertionDocument(document: BaasDocument): String?

    /**
     * Remove a BaasDocument representing an insertion from the server.
     *
     * @param document the document to remove
     *
     * @return true if the document was removed successfully, false otherwise
     */
    fun sendInsertionDeleteRequest(document: BaasDocument): Boolean

    /**
     * Send a BaasDocument representing conversation data to the server.
     *
     * @param document the document to send
     * @param partnerId the id of the other user participating in the conversation
     *
     * @return the String representing the id of the document if the document was sent successfully,
     *         null otherwise
     */
    fun sendConversationDocument(document: BaasDocument, partnerId: String): String?

    /**
     * Send a BaasDocument representing message data to the server.
     *
     * @param document the document to send
     * @param recipientId the id of the user receiving the message
     *
     * @return the String representing the id of the document if the document was sent successfully,
     *         null otherwise
     */
    fun sendMessageDocument(document: BaasDocument, recipientId: String): String?
}