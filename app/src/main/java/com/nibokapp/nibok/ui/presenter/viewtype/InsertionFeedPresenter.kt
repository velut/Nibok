package com.nibokapp.nibok.ui.presenter.viewtype

import android.util.Log
import com.nibokapp.nibok.domain.command.bookinsertion.feed.*
import com.nibokapp.nibok.domain.model.BookInsertionModel
import com.nibokapp.nibok.ui.adapter.viewtype.common.ViewType
import com.nibokapp.nibok.ui.presenter.viewtype.common.InsertionSaveStatusPresenter
import com.nibokapp.nibok.ui.presenter.viewtype.common.ViewTypePresenter

/**
 * Presenter that operates on the feed of books' insertions.
 */
class InsertionFeedPresenter(
        override val TAG: String = InsertionFeedPresenter.TAG
) : ViewTypePresenter, InsertionSaveStatusPresenter {

    companion object {
        private val TAG : String = InsertionSaveStatusPresenter::class.java.simpleName
    }

    override fun getData(): List<ViewType> {
        Log.d(TAG, "Getting feed data")
        return RequestBookInsertionFeedCommand().execute()
    }

    override fun getCachedData(): List<ViewType> {
        Log.d(TAG, "Getting cached feed data")
        return RequestCachedBookInsertionFeedCommand().execute()
    }

    override fun getDataNewerThanItem(item: ViewType): List<ViewType> {
        Log.d(TAG, "Getting newer feed data")
        return RequestNewerBookInsertionFeedCommand(item as BookInsertionModel).execute()
    }

    override fun getDataOlderThanItem(item: ViewType): List<ViewType> {
        Log.d(TAG, "Getting older feed data")
        return RequestOlderBookInsertionFeedCommand(item as BookInsertionModel).execute()
    }

    override fun getQueryData(query: String): List<ViewType> {
        Log.d(TAG, "Getting feed query data")
        return RequestBookInsertionFeedFromQueryCommand(query).execute()
    }
}