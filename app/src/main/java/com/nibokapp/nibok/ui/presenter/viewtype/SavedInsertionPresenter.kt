package com.nibokapp.nibok.ui.presenter.viewtype

import com.nibokapp.nibok.data.repository.BookManager
import com.nibokapp.nibok.ui.adapter.common.ViewType
import com.nibokapp.nibok.ui.presenter.viewtype.common.ViewTypePresenter

/**
 * Presenter that operates on insertions of saved books.
 */
class SavedInsertionPresenter : ViewTypePresenter {

    private var cachedData: List<ViewType> = emptyList()

    override fun getData(): List<ViewType> {
        cachedData = BookManager.getSavedBooksList()
        return cachedData
    }

    override fun getCachedData(): List<ViewType> = cachedData

    override fun getQueryData(query: String): List<ViewType> {
        // TODO change search?
        return BookManager.getBooksFromQuery(query)
    }
}