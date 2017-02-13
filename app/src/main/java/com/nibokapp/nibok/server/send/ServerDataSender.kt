package com.nibokapp.nibok.server.send

import android.net.Uri
import android.util.Log
import com.baasbox.android.*
import com.nibokapp.nibok.extension.onSuccessReturn
import com.nibokapp.nibok.server.send.common.ServerDataSenderInterface
import com.nibokapp.nibok.util.ImageCompressor

class ServerDataSender : ServerDataSenderInterface {

    companion object {
        private val TAG = ServerDataSender::class.java.simpleName

        /**
         * Access Control List allowing everyone, including anonymous users to read a document or file
         */
        private val ACL_PUBLIC_ACCESS = BaasACL.grantRole(Role.ANONYMOUS, Grant.READ)
    }

    /*
     * BOOK DATA
     */

    override fun sendBookDocument(document: BaasDocument): String? {
        Log.d(TAG, "Sending book to server")
        val sent = document.sendForPublicReading()
        return document.getIdOrNull(sent)
    }

    /*
     * INSERTIONS
     */

    override fun sendInsertionPictures(fileUris: List<Uri>): List<String>? {
        val pictureIds = mutableListOf<String>()
        for (fileUri in fileUris) {
            val compressedPicture = ImageCompressor.compressImage(fileUri) ?: return null
            Log.d(TAG, "Uploading picture: $fileUri")
            val pictureId = BaasFile().uploadSync(ACL_PUBLIC_ACCESS, compressedPicture).onSuccessReturn { it.id }
                    ?: return null
            Log.d(TAG, "Uploaded picture: $fileUri, got id: $pictureId")
            pictureIds += pictureId
            Log.d(TAG, "Uploaded ${pictureIds.size} pictures so far")
        }
        Log.d(TAG, "Uploaded all pictures (${pictureIds.size})")
        return pictureIds.toList()
    }

    override fun sendInsertionDocument(document: BaasDocument): String? {
        Log.d(TAG, "Sending insertion to server")
        val sent = document.sendForPublicReading()
        return document.getIdOrNull(sent)
    }

    override fun sendInsertionDeleteRequest(document: BaasDocument): Boolean {
        Log.d(TAG, "Removing insertion from server")
        return document.deleteSync().isSuccess
    }

    /*
     * CONVERSATIONS
     */

    override fun sendConversationDocument(document: BaasDocument, partnerId: String): String? {
        Log.d(TAG, "Sending conversation to server")
        // ACL allowing the conversation's partner to read and update the conversation document
        val acl = BaasACL.grantUser(partnerId, Grant.READ, Grant.UPDATE)
        val sent = document.saveSync(acl).isSuccess
        return document.getIdOrNull(sent)
    }

    /*
     * MESSAGES
     */

    override fun sendMessageDocument(document: BaasDocument, recipientId: String): String? {
        Log.d(TAG, "Sending message to server")
        // ACL allowing the message's recipient to read the message document
        val acl = BaasACL.grantUser(recipientId, Grant.READ)
        val sent = document.saveSync(acl).isSuccess
        return document.getIdOrNull(sent)
    }

    /*
     * EXTENSIONS
     */

    /**
     * Send this BaasDocument and let it be readable by everyone (including anonymous users).
     */
    private fun BaasDocument.sendForPublicReading(): Boolean {
        return this.saveSync(ACL_PUBLIC_ACCESS).isSuccess
    }

    /**
     * Get the id of this document if it was saved, null otherwise.
     */
    private fun BaasDocument.getIdOrNull(sent: Boolean): String? = if (sent) this.id else null
}
