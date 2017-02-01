package com.nibokapp.nibok.ui.presenter.main

import android.util.Log
import com.nibokapp.nibok.domain.command.bookinsertion.feed.RequestBookInsertionFeedCommand
import com.nibokapp.nibok.domain.command.bookinsertion.feed.RequestBookInsertionFeedFromQueryCommand
import com.nibokapp.nibok.domain.command.bookinsertion.feed.RequestCachedBookInsertionFeedCommand
import com.nibokapp.nibok.domain.command.bookinsertion.feed.RequestOlderBookInsertionFeedCommand
import com.nibokapp.nibok.domain.model.BookInsertionModel

/**
 * Presenter used for the insertions' feed fragment.
 */
class InsertionFeedPresenter : MainActivityPresenter<BookInsertionModel> {

    companion object {
        private val TAG = InsertionFeedPresenter::class.java.simpleName
    }

    override fun getData(): List<BookInsertionModel> {
        Log.d(TAG, "Requesting insertion feed data")
        return RequestBookInsertionFeedCommand().execute()
    }

    override fun getCachedData(): List<BookInsertionModel> {
        Log.d(TAG, "Requesting cached insertion feed data")
        return RequestCachedBookInsertionFeedCommand().execute()
    }

    override fun getDataOlderThanItem(item: BookInsertionModel): List<BookInsertionModel> {
        Log.d(TAG, "Requesting older insertion feed data")
        return RequestOlderBookInsertionFeedCommand(item).execute()
    }

    override fun getQueryData(query: String): List<BookInsertionModel> {
        Log.d(TAG, "Requesting insertion feed data by query")
        return RequestBookInsertionFeedFromQueryCommand(query).execute()
    }
}
