package com.nibokapp.nibok.ui.presenter.viewtype

import com.nibokapp.nibok.data.repository.BookInsertionRepository
import com.nibokapp.nibok.data.repository.BookManager
import com.nibokapp.nibok.data.repository.common.BookInsertionRepositoryInterface
import com.nibokapp.nibok.ui.adapter.viewtype.common.ViewType
import com.nibokapp.nibok.ui.presenter.viewtype.common.InsertionSaveStatusPresenter
import com.nibokapp.nibok.ui.presenter.viewtype.common.ViewTypePresenter

/**
 * Presenter that operates on the feed of books' insertions.
 */
class InsertionFeedPresenter(
        override val insertionRepository: BookInsertionRepositoryInterface = BookInsertionRepository
) : ViewTypePresenter, InsertionSaveStatusPresenter {

    private var cachedData: List<ViewType> = emptyList()

    override fun getData(): List<ViewType> {
        cachedData = BookManager.getFeedBooksList()
        return cachedData
    }

    override fun getCachedData(): List<ViewType> = cachedData

    override fun getQueryData(query: String): List<ViewType> {
        return BookManager.getBooksFromQuery(query)
    }
}