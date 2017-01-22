package com.nibokapp.nibok.server.mapper

import com.baasbox.android.BaasDocument
import com.baasbox.android.BaasUser
import com.baasbox.android.json.JsonArray
import com.nibokapp.nibok.data.db.*
import com.nibokapp.nibok.data.repository.server.common.ServerCollection
import com.nibokapp.nibok.data.repository.server.common.ServerConstants
import com.nibokapp.nibok.extension.getAvatar
import com.nibokapp.nibok.extension.parseDate
import com.nibokapp.nibok.extension.toRealmStringList
import com.nibokapp.nibok.extension.toStringList
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

    companion object {
        private val TAG = ServerDataMapper::class.java.simpleName
    }

    /*
     * USER
     */

    override fun convertUserFromServer(user: BaasUser?): ExternalUser? = user?.toExternalUser()

    /*
     * BOOK DATA
     */

    override fun convertDocumentToBook(document: BaasDocument?): Book? = document?.toBook()

    override fun convertBookToDocument(book: Book): BaasDocument = book.toDocument()

    /*
     * INSERTIONS
     */

    override fun convertDocumentListToInsertions(documents: List<BaasDocument>): List<Insertion> {
        return documents.map { convertDocumentToInsertion(it) }.filterNotNull()
    }

    override fun convertDocumentToInsertion(document: BaasDocument): Insertion? {
        val insertion = document.toInsertion()
        return if (insertion.isWellFormed()) insertion else null
    }

    override fun convertInsertionListToDocuments(insertions: List<Insertion>): List<BaasDocument> {
        return insertions.map { convertInsertionToDocument(it, it.book?.id ?: "") }
    }

    override fun convertInsertionToDocument(insertion: Insertion, bookId: String): BaasDocument =
            insertion.toDocument(bookId)

    /*
     * CONVERSATIONS
     */

    override fun convertDocumentListToConversations(documents: List<BaasDocument>): List<Conversation> {
        return documents.map { convertDocumentToConversation(it) }.filterNotNull()
    }

    override fun convertDocumentToConversation(document: BaasDocument): Conversation? {
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

    private fun BaasDocument.getDate(): Date = creationDate.parseDate()

    private fun BaasDocument.getSeller(): ExternalUser? {
        val sellerName = author // The author of the insertion is the seller
        val sellerAvatar = fetcher.fetchUserAvatar(sellerName)
        return ExternalUser(sellerName, sellerAvatar ?: "")
    }

    private fun BaasDocument.getBook(): Book? {
        val bookId = getString(ServerConstants.BOOK_ID)
        return convertDocumentToBook(fetcher.fetchBookDocumentById(bookId))
    }

    private fun BaasDocument.getPartner(): ExternalUser? {
        val participantsIds = getArray(ServerConstants.PARTICIPANTS).filterIsInstance<String>()
        val partnerId = participantsIds.find { it != getCurrentUserId() }
        return partnerId?.let { convertUserFromServer(fetcher.fetchUserById(it)) }
    }

    private fun BaasDocument.getLatestMessage(): Message? {
        val latestMessageDocument = fetcher.fetchLatestMessageByConversation(id) ?: return null
        return convertDocumentToMessage(latestMessageDocument)
    }

    private fun BaasUser.toExternalUser(): ExternalUser = with(this) {
        ExternalUser(name, getAvatar())
    }

    private fun BaasDocument.toBook(): Book = with(this) {
        val title = getString(ServerConstants.TITLE)
        val authors = getArray(ServerConstants.AUTHORS).filterIsInstance<String>().toRealmStringList()
        val year = getInt(ServerConstants.YEAR)
        val publisher = getString(ServerConstants.PUBLISHER)
        val isbn = getString(ServerConstants.ISBN)
        Book(id, title, authors, year, publisher, isbn)
    }

    private fun BaasDocument.toInsertion(): Insertion = with(this) {
        val date = getDate()
        val seller = getSeller()
        val book = getBook()
        val bookPrice = getFloat(ServerConstants.BOOK_PRICE)
        val bookCondition = getString(ServerConstants.BOOK_CONDITION)
        val bookPictures = getArray(ServerConstants.BOOK_PICTURES)
                .filterIsInstance<String>().toRealmStringList()
        Insertion(id, date, seller, book, bookPrice, bookCondition, bookPictures)
    }

    private fun BaasDocument.toConversation(): Conversation = with(this) {
        val userId = getCurrentUserId()
        val partner = getPartner()
        val date = getDate()
        val latestMessage = getLatestMessage()
        Conversation(id, userId, partner, latestMessage, date)
    }

    private fun BaasDocument.toMessage(): Message = with(this) {
        val conversationId = getString(ServerConstants.CONVERSATION_ID)
        val senderId = getString(ServerConstants.SENDER_ID)
        val text = getString(ServerConstants.TEXT)
        val date = getDate()
        Message(id, conversationId, senderId, text, date)
    }

    private fun getCurrentUserId(): String = BaasUser.current()?.name ?: ""

    private fun Book.toDocument(): BaasDocument {
        val document = BaasDocument(ServerCollection.BOOKS.id)
        with(ServerConstants) {
            document.put(TITLE, title)
                    .put(AUTHORS, authors.toStringList().toJsonArray())
                    .put(YEAR, year.toLong())
                    .put(PUBLISHER, publisher)
                    .put(ISBN, isbn)
        }
        return document
    }

    private fun Insertion.toDocument(bookId: String): BaasDocument {
        val document = BaasDocument(ServerCollection.INSERTIONS.id)
        with(ServerConstants) {
            document.put(BOOK_ID, bookId)
                    .put(BOOK_PRICE, bookPrice.toDouble())
                    .put(BOOK_CONDITION, bookCondition)
                    .put(BOOK_PICTURES, bookImagesSources.toStringList().toJsonArray())
        } // TODO Real picture urls
        return document
    }

    private fun List<String>.toJsonArray(): JsonArray {
        val array = JsonArray()
        this.forEach { array.add(it) }
        return array
    }

}