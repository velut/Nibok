package com.nibokapp.nibok.server.send.common

import com.baasbox.android.BaasDocument

interface ServerDataSenderInterface {

    /**
     * Send a BaasDocument representing book data to the server.
     *
     * @param document the document to send
     *
     * @return true if the document was sent successfully, false otherwise
     */
    fun sendBookDocument(document: BaasDocument) : Boolean

    /**
     * Send a BaasDocument representing insertion data to the server.
     *
     * @param document the document to send
     *
     * @return true if the document was sent successfully, false otherwise
     */
    fun sendInsertionDocument(document: BaasDocument) : Boolean
}