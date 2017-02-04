package com.nibokapp.nibok.ui.presenter.main

import android.util.Log
import com.nibokapp.nibok.domain.command.bookinsertion.saved.RequestCachedSavedBookInsertionCommand
import com.nibokapp.nibok.domain.command.bookinsertion.saved.RequestOlderSavedBookInsertionCommand
import com.nibokapp.nibok.domain.command.bookinsertion.saved.RequestSavedBookInsertionCommand
import com.nibokapp.nibok.domain.command.bookinsertion.saved.RequestSavedBookInsertionFromQueryCommand
import com.nibokapp.nibok.domain.model.BookInsertionModel
import com.nibokapp.nibok.ui.presenter.viewtype.common.InsertionSaveStatusPresenter

/**
 * Presenter used for the insertions' feed fragment.
 */
class InsertionBookmarkPresenter : MainActivityPresenter<BookInsertionModel>, InsertionSaveStatusPresenter {

    companion object {
        private val TAG = InsertionBookmarkPresenter::class.java.simpleName
    }

    override fun getData(): List<BookInsertionModel> {
        Log.d(TAG, "Requesting insertion saved data")
        return RequestSavedBookInsertionCommand().execute()
    }

    override fun getCachedData(): List<BookInsertionModel> {
        Log.d(TAG, "Requesting cached insertion saved data")
        return RequestCachedSavedBookInsertionCommand().execute()
    }

    override fun getDataOlderThanItem(item: BookInsertionModel): List<BookInsertionModel> {
        Log.d(TAG, "Requesting older insertion saved data")
        return RequestOlderSavedBookInsertionCommand(item).execute()
    }

    override fun getQueryData(query: String): List<BookInsertionModel> {
        Log.d(TAG, "Requesting insertion saved data by query")
        return RequestSavedBookInsertionFromQueryCommand(query).execute()
    }
}
