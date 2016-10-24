package com.nibokapp.nibok.server.mapper.common

import com.baasbox.android.BaasDocument
import com.baasbox.android.BaasUser
import com.nibokapp.nibok.data.db.*

/**
 * ServerDataMapperInterface represents an interface for objects that can map data from the server
 * into data for the local database and vice versa.
 */
interface ServerDataMapperInterface {

    /**
     * Convert a BaasUser from the server into an ExternalUser.
     *
     * @param user user data from the server
     *
     * @return an ExternalUser if the conversion was successful, null otherwise
     */
    fun convertUserFromServer(user: BaasUser?) : ExternalUser?

    /**
     * Convert a BaasDocument containing book data into a Book.
     *
     * @param document the document from the server
     *
     * @return a Book if the conversion was successful, null otherwise
     */
    fun convertDocumentToBook(document: BaasDocument?) : Book?

    /**
     * Convert a Book into a BaasDocument containing book data.
     *
     * @param book the Book from the local db
     *
     * @return a BaasDocument
     */
    fun convertBookToDocument(book: Book) : BaasDocument

    /**
     * Convert a list of BaasDocument containing insertion data into a list of Insertion.
     *
     * @param documents the list of documents from the server
     *
     * @return a list of Insertion
     */
    fun convertDocumentListToInsertions(documents: List<BaasDocument>) : List<Insertion>

    /**
     * Convert a BaasDocument containing insertion data into an Insertion.
     *
     * @param document the document from the server
     *
     * @return an Insertion if the conversion was successful, null otherwise
     */
    fun convertDocumentToInsertion(document: BaasDocument) : Insertion?

    /**
     * Convert a list of Insertion into a list of BaasDocument containing insertion data.
     *
     * @param insertions the list of insertions from the local db
     *
     * @return a list of BaasDocument
     */
    fun convertInsertionListToDocuments(insertions: List<Insertion>) : List<BaasDocument>

    /**
     * Convert an Insertion into a BaasDocument containing insertion data.
     *
     * @param insertion the Insertion from the local db
     *
     * @return an Insertion
     */
    fun convertInsertionToDocument(insertion: Insertion, bookId: String) : BaasDocument

    /**
     * Convert a list of BaasDocument containing conversation data into a list of Conversation.
     *
     * @param documents the list of documents from the server
     *
     * @return a list of Conversation
     */
    fun convertDocumentListToConversations(documents: List<BaasDocument>) : List<Conversation>

    /**
     * Convert a BaasDocument containing insertion data into a Conversation.
     *
     * @param document the document from the server
     *
     * @return a Conversation if the conversion was successful, null otherwise
     */
    fun convertDocumentToConversation(document: BaasDocument) : Conversation?

    /**
     * Convert a list of BaasDocument containing message data into a list of Message.
     *
     * @param documents the list of documents from the server
     *
     * @return a list of Message
     */
    fun convertDocumentListToMessages(documents: List<BaasDocument>): List<Message>

    /**
     * Convert a BaasDocument containing message data into a Message.
     *
     * @param document the document from the server
     *
     * @return a Message if the conversion was successful, null otherwise
     */
    fun convertDocumentToMessage(document: BaasDocument): Message?
}