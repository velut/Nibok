package com.nibokapp.nibok.server.send.common

import android.net.Uri
import com.baasbox.android.BaasDocument

interface ServerDataSenderInterface {

    /**
     * Send the pictures taken for an insertion to the server.
     *
     * @param fileUris the list of Uri associated to the pictures
     *
     * @return a Pair<Boolean, List<String>?> that is (true, list) if pictures were sent successfully,
     *         (false, null) otherwise
     */
    fun sendInsertionPictures(fileUris: List<Uri>): Pair<Boolean, List<String>?>

    /**
     * Send a BaasDocument representing book data to the server.
     *
     * @param document the document to send
     *
     * @return true if the document was sent successfully, false otherwise
     */
    fun sendBookDocument(document: BaasDocument): Pair<Boolean, String?>

    /**
     * Send a BaasDocument representing insertion data to the server.
     *
     * @param document the document to send
     *
     * @return true if the document was sent successfully, false otherwise
     */
    fun sendInsertionDocument(document: BaasDocument): Pair<Boolean, String?>

    /**
     * Send a BaasDocument representing conversation data to the server.
     *
     * @param document the document to send
     * @param partnerId the id of the other user participating in the conversation
     *
     * @return true if the document was sent successfully, false otherwise
     */
    fun sendConversationDocument(document: BaasDocument, partnerId: String): Pair<Boolean, String?>

    /**
     * Send a BaasDocument representing message data to the server.
     *
     * @param document the document to send
     * @param recipientId the id of the user receiving the message
     *
     * @return true if the document was sent successfully, false otherwise
     */
    fun sendMessageDocument(document: BaasDocument, recipientId: String): Pair<Boolean, String?>
}