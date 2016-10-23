package com.nibokapp.nibok.server.mapper

import com.baasbox.android.BaasDocument
import com.baasbox.android.BaasUser
import com.baasbox.android.json.JsonArray
import com.nibokapp.nibok.data.db.*
import com.nibokapp.nibok.data.repository.server.common.ServerCollection
import com.nibokapp.nibok.data.repository.server.common.ServerConstants
import com.nibokapp.nibok.extension.*
import com.nibokapp.nibok.server.fetch.ServerDataFetcher
import com.nibokapp.nibok.server.fetch.common.ServerDataFetcherInterface
import com.nibokapp.nibok.server.mapper.common.ServerDataMapperInterface
import java.util.*

/**
 * ServerDataMapper maps data from the server into data used by the local db and vice versa.
 *
 * @param fetcher the fetcher used to get additional data stored in the documents from the server.
 * ServerDataFetcher is the default one.
 */
class ServerDataMapper(
        private val fetcher: ServerDataFetcherInterface = ServerDataFetcher()
) : ServerDataMapperInterface {

    /*
     * USER
     */

    override fun convertUserFromServer(user: BaasUser?) : ExternalUser? = user?.toExternalUser()

    /*
     * BOOK DATA
     */

    override fun convertDocumentToBook(document: BaasDocument?) : Book? = document?.toBook()

    override fun convertBookToDocument(book: Book): BaasDocument = book.toDocument()

    /*
     * INSERTIONS
     */

    override fun convertDocumentListToInsertions(documents: List<BaasDocument>) : List<Insertion> {
        return documents.map { convertDocumentToInsertion(it) }.filterNotNull()
    }

    override fun convertDocumentToInsertion(document: BaasDocument) : Insertion? {
        val insertion = document.toInsertion()
        return if (insertion.isWellFormed()) insertion else null
    }

    override fun convertInsertionListToDocuments(insertions: List<Insertion>): List<BaasDocument> {
        return insertions.map { convertInsertionToDocument(it) }
    }

    override fun convertInsertionToDocument(insertion: Insertion): BaasDocument = insertion.toDocument()

    /*
     * CONVERSATIONS
     */

    override fun convertDocumentListToConversations(documents: List<BaasDocument>) : List<Conversation> {
        return documents.map { convertDocumentToConversation(it) }.filterNotNull()
    }

    override fun convertDocumentToConversation(document: BaasDocument) : Conversation? {
        val conversation = document.toConversation()
        return if (conversation.isWellFormed()) conversation else null
    }

    /*
     * MESSAGES
     */

    override fun convertDocumentListToMessages(documents: List<BaasDocument>): List<Message> {
        return documents.map { convertDocumentToMessage(it) }.filterNotNull()
    }

    override fun convertDocumentToMessage(document: BaasDocument): Message? {
        val message = document.toMessage()
        return if (message.isWellFormed()) message else null
    }

    /*
     * EXTENSIONS
     */

    private fun BaasDocument.getDate() : Date = creationDate.parseDate()

    private fun BaasDocument.getSeller() : ExternalUser? {
        val sellerId = author // The author of the insertion is the seller
        return convertUserFromServer(fetcher.fetchUserById(sellerId))
    }

    private fun BaasDocument.getBook() : Book? {
        val isbn = getString(ServerConstants.ISBN)
        return convertDocumentToBook(fetcher.fetchBookDocumentByISBN(isbn))
    }

    private fun BaasDocument.getPartner() : ExternalUser? {
        val participantsIds = getArray(ServerConstants.PARTICIPANTS).filterIsInstance<String>()
        val partnerId = participantsIds.find { it != getCurrentUserId() }
        return partnerId?.let { convertUserFromServer(fetcher.fetchUserById(it)) }
    }

    private fun BaasDocument.getMessages() : List<Message> {
        val messageIds = getArray(ServerConstants.MESSAGES)
        return convertDocumentListToMessages(fetcher.fetchMessageDocumentList(messageIds))
    }

    private fun BaasUser.toExternalUser() : ExternalUser = with(this) {
        ExternalUser(name, getAvatar())
    }

    private fun BaasDocument.toBook() : Book = with(this) {
        val title = getString(ServerConstants.TITLE)
        val authors = getArray(ServerConstants.AUTHORS).filterIsInstance<String>().toRealmStringList()
        val year = getInt(ServerConstants.YEAR)
        val publisher = getString(ServerConstants.PUBLISHER)
        val isbn = getString(ServerConstants.ISBN)
        Book(title, authors, year, publisher, isbn)
    }

    private fun BaasDocument.toInsertion() : Insertion = with(this) {
        val date = getDate()
        val seller = getSeller()
        val book = getBook()
        val bookPrice = getFloat(ServerConstants.BOOK_PRICE)
        val bookCondition = getString(ServerConstants.BOOK_CONDITION)
        val bookPictures = getArray(ServerConstants.BOOK_PICTURES)
                .filterIsInstance<String>().toRealmStringList()
        Insertion(id, date, seller, book, bookPrice, bookCondition, bookPictures)
    }

    private fun BaasDocument.toConversation() : Conversation = with(this) {
        val userId = getCurrentUserId()
        val partner = getPartner()
        val date = getDate()
        val messages = getMessages().toRealmList()
        Conversation(id, userId, partner, date, messages)
    }

    private fun BaasDocument.toMessage(): Message = with(this) {
        val conversationId = getString(ServerConstants.CONVERSATION_ID)
        val senderId = getString(ServerConstants.SENDER_ID)
        val text = getString(ServerConstants.TEXT)
        val date = getDate()
        Message(conversationId, senderId, text, date)
    }

    private fun getCurrentUserId() : String = BaasUser.current()?.name ?: ""

    private fun Book.toDocument() : BaasDocument {
        val document = BaasDocument(ServerCollection.BOOKS.id)
        with(ServerConstants) {
            document.put(TITLE, title)
                    .put(AUTHORS, JsonArray.of(authors.toStringList()))
                    .put(YEAR, year.toLong())
                    .put(PUBLISHER, publisher)
                    .put(ISBN, isbn)
        }
        return document
    }

    private fun Insertion.toDocument() : BaasDocument {
        val document = BaasDocument(ServerCollection.INSERTIONS.id)
        with(ServerConstants) {
            document.put(BOOK_ISBN, book!!.isbn)
                    .put(BOOK_PRICE, bookPrice.toDouble())
                    .put(BOOK_CONDITION, bookCondition)
                    .put(BOOK_PICTURES, JsonArray.of(bookImagesSources.toStringList()))
        } // TODO Real picture urls
        return document
    }

}