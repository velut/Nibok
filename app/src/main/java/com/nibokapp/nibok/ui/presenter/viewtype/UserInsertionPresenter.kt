package com.nibokapp.nibok.ui.presenter.viewtype

import android.util.Log
import com.nibokapp.nibok.domain.command.bookinsertion.published.*
import com.nibokapp.nibok.domain.model.BookInsertionModel
import com.nibokapp.nibok.ui.adapter.viewtype.common.ViewType
import com.nibokapp.nibok.ui.presenter.viewtype.common.ViewTypePresenter

/**
 * Presenter that operates on the insertions published by the user.
 */
class UserInsertionPresenter: ViewTypePresenter {

    companion object {
        private val TAG = UserInsertionPresenter::class.java.simpleName
    }

    override fun getData(): List<ViewType> {
        Log.d(TAG, "Getting published data")
        return RequestPublishedBookInsertionCommand().execute()
    }

    override fun getCachedData(): List<ViewType> {
        Log.d(TAG, "Getting cached published data")
        return RequestCachedPublishedBookInsertionCommand().execute()
    }

    override fun getDataNewerThanItem(item: ViewType): List<ViewType> {
        Log.d(TAG, "Getting newer published data")
        return RequestNewerPublishedBookInsertionCommand(item as BookInsertionModel).execute()
    }

    override fun getDataOlderThanItem(item: ViewType): List<ViewType> {
        Log.d(TAG, "Getting older published data")
        return RequestOlderPublishedBookInsertionCommand(item as BookInsertionModel).execute()
    }

    override fun getQueryData(query: String): List<ViewType> {
        Log.d(TAG, "Getting published query data")
        return RequestPublishedBookInsertionFromQueryCommand(query).execute()
    }
}