package com.nibokapp.nibok.ui.presenter.main

import android.util.Log
import com.nibokapp.nibok.domain.command.bookinsertion.published.RequestCachedPublishedBookInsertionCommand
import com.nibokapp.nibok.domain.command.bookinsertion.published.RequestOlderPublishedBookInsertionCommand
import com.nibokapp.nibok.domain.command.bookinsertion.published.RequestPublishedBookInsertionCommand
import com.nibokapp.nibok.domain.command.bookinsertion.published.RequestPublishedBookInsertionFromQueryCommand
import com.nibokapp.nibok.domain.model.BookInsertionModel

/**
 * Presenter used for the user published insertions.
 */
class InsertionPublishedPresenter : MainActivityPresenter<BookInsertionModel>, InsertionDeletePresenter {

    companion object {
        private val TAG = InsertionPublishedPresenter::class.java.simpleName
    }

    override fun getData(): List<BookInsertionModel> {
        Log.d(TAG, "Requesting insertion published data")
        return RequestPublishedBookInsertionCommand().execute()
    }

    override fun getCachedData(): List<BookInsertionModel> {
        Log.d(TAG, "Requesting cached insertion published data")
        return RequestCachedPublishedBookInsertionCommand().execute()
    }

    override fun getDataOlderThanItem(item: BookInsertionModel): List<BookInsertionModel> {
        Log.d(TAG, "Requesting older insertion published data")
        return RequestOlderPublishedBookInsertionCommand(item).execute()
    }

    override fun getQueryData(query: String): List<BookInsertionModel> {
        Log.d(TAG, "Requesting insertion published data by query")
        return RequestPublishedBookInsertionFromQueryCommand(query).execute()
    }
}
