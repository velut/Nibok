package com.nibokapp.nibok.ui.presenter.viewtype

import android.util.Log
import com.nibokapp.nibok.domain.command.bookinsertion.saved.*
import com.nibokapp.nibok.domain.model.BookInsertionModel
import com.nibokapp.nibok.ui.adapter.viewtype.common.ViewType
import com.nibokapp.nibok.ui.presenter.viewtype.common.InsertionSaveStatusPresenter
import com.nibokapp.nibok.ui.presenter.viewtype.common.ViewTypePresenter

/**
 * Presenter that operates on insertions of saved books.
 */
class SavedInsertionPresenter : ViewTypePresenter, InsertionSaveStatusPresenter {

    companion object {
        private val TAG = SavedInsertionPresenter::class.java.simpleName
    }

    override fun getData(): List<ViewType> {
        Log.d(TAG, "Getting saved data")
        return RequestSavedBookInsertionCommand().execute()
    }

    override fun getCachedData(): List<ViewType> {
        Log.d(TAG, "Getting cached saved data")
        return RequestCachedSavedBookInsertionCommand().execute()
    }

    override fun getDataNewerThanItem(item: ViewType): List<ViewType> {
        Log.d(TAG, "Getting newer saved data")
        return RequestNewerSavedBookInsertionCommand(item as BookInsertionModel).execute()
    }

    override fun getDataOlderThanItem(item: ViewType): List<ViewType> {
        Log.d(TAG, "Getting older saved data")
        return RequestOlderSavedBookInsertionCommand(item as BookInsertionModel).execute()
    }

    override fun getQueryData(query: String): List<ViewType> {
        Log.d(TAG, "Getting query saved data")
        return RequestSavedBookInsertionFromQueryCommand(query).execute()
    }
}