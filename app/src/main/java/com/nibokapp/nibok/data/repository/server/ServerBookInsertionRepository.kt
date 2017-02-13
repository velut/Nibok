package com.nibokapp.nibok.data.repository.server

import android.net.Uri
import android.util.Log
import com.baasbox.android.BaasDocument
import com.baasbox.android.BaasUser
import com.baasbox.android.BaasUser.current
import com.nibokapp.nibok.data.db.Book
import com.nibokapp.nibok.data.db.Insertion
import com.nibokapp.nibok.data.repository.common.BookInsertionRepositoryInterface
import com.nibokapp.nibok.extension.getSavedInsertionsIdList
import com.nibokapp.nibok.extension.toStringList
import com.nibokapp.nibok.extension.toggleInsertionSaveStatus
import com.nibokapp.nibok.server.fetch.ServerDataFetcher
import com.nibokapp.nibok.server.fetch.common.ServerDataFetcherInterface
import com.nibokapp.nibok.server.mapper.ServerDataMapper
import com.nibokapp.nibok.server.mapper.common.ServerDataMapperInterface
import com.nibokapp.nibok.server.send.ServerDataSender
import com.nibokapp.nibok.server.send.common.ServerDataSenderInterface

/**
 * Server repository for book insertions.
 */
object ServerBookInsertionRepository: BookInsertionRepositoryInterface {

    const private val TAG = "ServerBookInsertionRepo"

    // Fetcher and mapper used to retrieve data from the server
    // and map it into data for the local db
    private val fetcher: ServerDataFetcherInterface = ServerDataFetcher()
    private val sender: ServerDataSenderInterface = ServerDataSender()
    private val mapper: ServerDataMapperInterface = ServerDataMapper()

    // Current logged in user
    private val currentUser: BaasUser?
        get() = current()

    // Caches
    private var feedCache: List<Insertion> = emptyList()
    private var savedCache: List<Insertion> = emptyList()
    private var publishedCache: List<Insertion> = emptyList()

    /*
     * COMMON FUNCTIONS
     */

    override fun getInsertionById(insertionId: String): Insertion? {
        Log.d(TAG, "Getting insertion with id: $insertionId")
        val result = fetcher.fetchInsertionDocumentById(insertionId)
        return result?.let { mapper.convertDocumentToInsertion(it) }
    }

    override fun getBookByISBN(isbn: String): Book? {
        Log.d(TAG, "Getting book with ISBN: $isbn")
        val result = fetcher.fetchBookDocumentByISBN(isbn)
        return result?.let { mapper.convertDocumentToBook(it) }
    }

    override fun getInsertionListFromQuery(query: String): List<Insertion> {
        val insertions = fetcher.fetchInsertionDocumentListByQuery(query).toInsertionList()
        Log.d(TAG, "Found ${insertions.size} insertions corresponding to query: $query")
        return insertions
    }

    /*
     * FEED BOOK INSERTIONS
     */

    override fun getFeedInsertionList(cached: Boolean): List<Insertion> {
        if (cached) return feedCache
        feedCache = fetcher.fetchRecentInsertionDocumentList(true, excludeAllByUser = true).toInsertionList()
        Log.d(TAG, "Found ${feedCache.size} feed insertions")
        return feedCache
    }

    override fun getFeedInsertionListFromQuery(query: String): List<Insertion> {
        val insertions = fetcher.fetchInsertionDocumentListByQuery(query, true, excludeAllByUser = true).toInsertionList()
        Log.d(TAG, "Found ${insertions.size} feed insertions corresponding to query: $query")
        return insertions
    }

    override fun getFeedInsertionListOlderThanInsertion(insertionId: String): List<Insertion> {
        Log.d(TAG, "Getting feed insertions older than: $insertionId")
        val insertions = fetcher
                .fetchInsertionDocumentListAfterDateOfInsertion(insertionId, true, excludeAllByUser = true)
                .toInsertionList()
        Log.d(TAG, "Found ${insertions.size} feed insertions older than: $insertionId")
        return insertions
    }

    /*
     * SAVED BOOK INSERTIONS
     */

    override fun getSavedInsertionList(cached: Boolean): List<Insertion> {
        if (cached) return savedCache
        savedCache = fetcher.fetchRecentInsertionDocumentList(true, includeOnlyIfSaved = true).toInsertionList()
        Log.d(TAG, "Found ${savedCache.size} saved insertions")
        return savedCache
    }

    override fun getSavedInsertionListFromQuery(query: String): List<Insertion> {
        val insertions = fetcher.fetchInsertionDocumentListByQuery(query, true, includeOnlyIfSaved = true).toInsertionList()
        Log.d(TAG, "Found ${insertions.size} saved insertions corresponding to query: $query")
        return insertions
    }

    override fun getSavedInsertionListOlderThanInsertion(insertionId: String): List<Insertion> {
        Log.d(TAG, "Getting saved insertions older than: $insertionId")
        val insertions = fetcher
                .fetchInsertionDocumentListAfterDateOfInsertion(insertionId, true, includeOnlyIfSaved= true)
                .toInsertionList()
        Log.d(TAG, "Found ${insertions.size} saved insertions older than: $insertionId")
        return insertions
    }

    /*
     * PUBLISHED BOOK INSERTIONS
     */

    override fun getPublishedInsertionList(cached: Boolean): List<Insertion> {
        if (cached) return publishedCache
        publishedCache = fetcher.fetchRecentInsertionDocumentList(true, includeOnlyByUser = true).toInsertionList()
        Log.d(TAG, "Found ${publishedCache.size} published insertions")
        return publishedCache
    }

    override fun getPublishedInsertionListFromQuery(query: String): List<Insertion> {
        val insertions = fetcher.fetchInsertionDocumentListByQuery(query, true, includeOnlyByUser = true).toInsertionList()
        Log.d(TAG, "Found ${insertions.size} published insertions corresponding to query: $query")
        return insertions
    }

    override fun getPublishedInsertionListOlderThanInsertion(insertionId: String): List<Insertion> {
        Log.d(TAG, "Getting published insertions older than: $insertionId")
        val insertions = fetcher
                .fetchInsertionDocumentListAfterDateOfInsertion(insertionId, true, includeOnlyByUser= true)
                .toInsertionList()
        Log.d(TAG, "Found ${insertions.size} published insertions older than: $insertionId")
        return insertions
    }

    override fun deletePublishedInsertion(insertionId: String): Boolean {
        Log.d(TAG, "Deleting insertion: $insertionId")
        val insertion = fetcher.fetchInsertionDocumentById(insertionId) ?: return false
        return sender.sendInsertionDeleteRequest(insertion)
    }

    /*
     * BOOK INSERTION SAVE STATUS
     */

    override fun isBookInsertionSaved(insertionId: String): Boolean {
        val saveInsertionIds = currentUser?.getSavedInsertionsIdList() ?: emptyList()
        val isSaved = insertionId in saveInsertionIds
        Log.d(TAG, "Insertion: $insertionId is saved: $isSaved")
        return isSaved
    }

    override fun toggleInsertionSaveStatus(insertionId: String): Boolean {
        val user = currentUser ?:
                throw IllegalStateException("No user logged in. Cannot toggle insertion save status")
        val insertion = getInsertionById(insertionId)
        val insertionAuthor = insertion?.seller?.username
        if (user.name == insertionAuthor) {
            Log.d(TAG, "A seller can't bookmark his own insertion")
            return false
        }
        val saved = user.toggleInsertionSaveStatus(insertionId)
        Log.d(TAG, "After toggle: Save status: $saved")
        return saved
    }

    /*
     * BOOK INSERTION PUBLISHING
     */

    override fun publishInsertion(insertion: Insertion): String? {
        if (currentUser == null) return null

        val book = insertion.book ?: return null
        val bookId = publishBook(book) ?: return null

        val pictureUris = insertion.bookImagesSources.toStringList()
        val pictureIds = if (pictureUris.isNotEmpty()) {
            publishPictures(pictureUris) ?: return null
        } else {
            emptyList()
        }

        Log.d(TAG, "Publishing insertion")

        val insertionDoc = mapper.convertInsertionToDocument(insertion, bookId, pictureIds)
        val publishedInsertionId = sender.sendInsertionDocument(insertionDoc)
        Log.d(TAG, "Insertion: $insertion was published with id: $publishedInsertionId")
        return publishedInsertionId
    }

    /**
     * Eventually publish a Book on the server.
     *
     * @param book the book to publish
     *
     * @return a String representing the id of the book on the server
     * if the book was already previously published or correctly published now,
     * null if the book could not be published
     */
    private fun publishBook(book: Book): String? {
        val bookId = book.id

        // If the bookId is not "" then it was already assigned by the server
        val bookExistsOnServer = bookId != ""

        if (bookExistsOnServer) return bookId

        Log.d(TAG, "Publishing book with ISBN: ${book.isbn}")

        val bookDoc = mapper.convertBookToDocument(book)
        return sender.sendBookDocument(bookDoc)
    }

    /**
     * Publish the pictures associated with an insertion.
     */
    private fun  publishPictures(pictureUris: List<String>): List<String>? {
        Log.d(TAG, "Publishing ${pictureUris.size} pictures")
        val parsedPictureUris = pictureUris.map { Uri.parse(it) }
        Log.d(TAG, "Picture uris are:\n  $parsedPictureUris")
        return sender.sendInsertionPictures(parsedPictureUris)
    }

    /*
     * EXTENSIONS
     */

    fun List<BaasDocument>.toInsertionList() = mapper.convertDocumentListToInsertions(this)

}
