package com.nibokapp.nibok.ui.presenter.viewtype

import com.nibokapp.nibok.data.repository.BookManager
import com.nibokapp.nibok.ui.adapter.common.ViewType
import com.nibokapp.nibok.ui.presenter.viewtype.common.InsertionSaveStatusPresenter
import com.nibokapp.nibok.ui.presenter.viewtype.common.ViewTypePresenter

/**
 * Presenter that operates on the feed of books' insertions.
 */
class InsertionFeedPresenter : ViewTypePresenter, InsertionSaveStatusPresenter {

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