package com.nibokapp.nibok.data.repository

import android.util.Log
import com.nibokapp.nibok.data.db.Book
import com.nibokapp.nibok.data.db.Insertion
import com.nibokapp.nibok.data.repository.common.BookInsertionRepositoryInterface
import com.nibokapp.nibok.data.repository.db.LocalBookInsertionRepository
import com.nibokapp.nibok.data.repository.server.ServerBookInsertionRepository
import com.nibokapp.nibok.extension.firstListResultOrNullWithStorage
import com.nibokapp.nibok.extension.firstResultOrNull
import com.nibokapp.nibok.extension.storeAndReturnResult
import org.jetbrains.anko.doAsync

/**
 * Repository for book insertions.
 */
object BookInsertionRepository : BookInsertionRepositoryInterface {

    const private val TAG = "BookInsertionRepository"

    /**
     * Sources for this repository
     */
    private val localRepository = LocalBookInsertionRepository
    private val serverRepository = ServerBookInsertionRepository
    private val SOURCES = listOf(localRepository, serverRepository)

    private var feedCache: List<Insertion> = emptyList()
    private var savedCache: List<Insertion> = emptyList()
    private var publishedCache: List<Insertion> = emptyList()

    /*
     * COMMON FUNCTIONS
     */

    override fun getInsertionById(insertionId: String): Insertion? {
        Log.d(TAG, "Getting insertion with id: $insertionId")
        return SOURCES.firstResultOrNull { it.getInsertionById(insertionId) }
                .storeAndReturnResult { i, s -> storeInLocalRepo(i, s) }
    }

    override fun getBookByISBN(isbn: String): Book? {
        Log.d(TAG, "Getting book with ISBN: $isbn")
        return SOURCES.firstResultOrNull { it.getBookByISBN(isbn) }.first
    }

    override fun getInsertionListFromQuery(query: String): List<Insertion> {
        val results = SOURCES.firstListResultOrNullWithLocalStorage {
            it.getInsertionListFromQuery(query)
        } ?: emptyList()
        Log.d(TAG, "Book insertions corresponding to query '$query': ${results.size}")
        return results
    }

    /*
     * FEED BOOK INSERTIONS
     */

    override fun getFeedInsertionList(cached: Boolean): List<Insertion> {
        if (cached) return feedCache
        feedCache = SOURCES.reversed().firstListResultOrNullWithLocalStorage {
            it.getFeedInsertionList(cached)
        } ?: emptyList()
        Log.d(TAG, "Found ${feedCache.size} feed insertions")
        return feedCache
    }

    override fun getFeedInsertionListFromQuery(query: String): List<Insertion> {
        return SOURCES.firstListResultOrNullWithLocalStorage {
            it.getFeedInsertionListFromQuery(query)
        } ?: emptyList()
    }

    override fun getFeedInsertionListOlderThanInsertion(insertionId: String): List<Insertion> {
        return SOURCES.firstListResultOrNullWithLocalStorage {
            it.getFeedInsertionListOlderThanInsertion(insertionId)
        } ?: emptyList()
    }

    /*
     * SAVED BOOK INSERTIONS
     */

    override fun getSavedInsertionList(cached: Boolean): List<Insertion> {
        if (cached) return savedCache
        savedCache = SOURCES.reversed().firstListResultOrNullWithLocalStorage {
            it.getSavedInsertionList(cached)
        } ?: emptyList()
        return savedCache
    }

    override fun getSavedInsertionListFromQuery(query: String): List<Insertion> {
        return SOURCES.firstListResultOrNullWithLocalStorage {
            it.getSavedInsertionListFromQuery(query)
        } ?: emptyList()
    }

    override fun getSavedInsertionListOlderThanInsertion(insertionId: String): List<Insertion> {
        return SOURCES.firstListResultOrNullWithLocalStorage {
            it.getSavedInsertionListOlderThanInsertion(insertionId)
        } ?: emptyList()
    }

    /*
     * PUBLISHED BOOK INSERTIONS
     */

    override fun getPublishedInsertionList(cached: Boolean): List<Insertion> {
        if (cached) return publishedCache
        publishedCache = SOURCES.reversed().firstListResultOrNullWithLocalStorage {
            it.getPublishedInsertionList(cached)
        } ?: emptyList()
        return publishedCache
    }

    override fun getPublishedInsertionListFromQuery(query: String): List<Insertion> {
        return SOURCES.firstListResultOrNullWithLocalStorage {
            it.getPublishedInsertionListFromQuery(query)
        } ?: emptyList()
    }

    override fun getPublishedInsertionListOlderThanInsertion(insertionId: String): List<Insertion> {
        return SOURCES.firstListResultOrNullWithLocalStorage {
            it.getPublishedInsertionListOlderThanInsertion(insertionId)
        } ?: emptyList()
    }

    override fun deletePublishedInsertion(insertionId: String): Boolean {
        val isDeleted = serverRepository.deletePublishedInsertion(insertionId)
        if (isDeleted) {
            doAsync { localRepository.deletePublishedInsertion(insertionId) }
        }
        return isDeleted
    }

    /*
     * BOOK INSERTION SAVE STATUS
     */

    override fun isBookInsertionSaved(insertionId: String): Boolean {
        return SOURCES.reversed().firstResultOrNull { it.isBookInsertionSaved(insertionId) }.first
                ?: false
    }

    override fun toggleInsertionSaveStatus(insertionId: String): Boolean {
        val savedOnServer = serverRepository.toggleInsertionSaveStatus(insertionId)
        doAsync { localRepository.setInsertionSaveStatus(insertionId, savedOnServer) }
        return savedOnServer
    }

    /*
     * BOOK INSERTION PUBLISHING
     */

    override fun publishInsertion(insertion: Insertion): String? {
        return serverRepository.publishInsertion(insertion)
    }

    /*
     * Utilities
     */

    private fun storeInLocalRepo(insertion: Insertion?, source: BookInsertionRepositoryInterface?) {
        if (source == serverRepository && insertion != null) {
            localRepository.storeItem(insertion)
        }
    }

    private fun storeInLocalRepo(insertionList: List<Insertion>?, source: BookInsertionRepositoryInterface?) {
        if (source == serverRepository && insertionList != null) {
            localRepository.storeItems(insertionList)
        }
    }

    /**
     * Shorthand for querying the repositories first
     * and then trying to store new data in the local repository.
     */
    private inline fun Iterable<BookInsertionRepositoryInterface>.firstListResultOrNullWithLocalStorage(
            predicate: (BookInsertionRepositoryInterface) -> List<Insertion>?): List<Insertion>? {
        return this.firstListResultOrNullWithStorage(
                { predicate(it) },
                { insertionList, source -> storeInLocalRepo(insertionList, source) }
        )
    }
}