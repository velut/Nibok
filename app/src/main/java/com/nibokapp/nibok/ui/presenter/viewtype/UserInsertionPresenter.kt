package com.nibokapp.nibok.ui.presenter.viewtype

import com.nibokapp.nibok.ui.adapter.viewtype.common.ViewType
import com.nibokapp.nibok.ui.presenter.viewtype.common.ViewTypePresenter

/**
 * Presenter that operates on the insertions published by the user.
 */
class UserInsertionPresenter: ViewTypePresenter {

    private var cachedData: List<ViewType> = emptyList()

    override fun getData(): List<ViewType> {
        return emptyList()
    }

    override fun getCachedData(): List<ViewType> = cachedData

    override fun getQueryData(query: String): List<ViewType> {
        return emptyList()
    }
}