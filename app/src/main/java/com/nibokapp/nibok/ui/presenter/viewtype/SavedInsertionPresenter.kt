package com.nibokapp.nibok.ui.presenter.viewtype

import android.util.Log
import com.nibokapp.nibok.domain.command.bookinsertion.saved.RequestCachedSavedBookInsertionCommand
import com.nibokapp.nibok.domain.command.bookinsertion.saved.RequestSavedBookInsertionCommand
import com.nibokapp.nibok.domain.command.bookinsertion.saved.RequestSavedBookInsertionFromQueryCommand
import com.nibokapp.nibok.ui.adapter.viewtype.common.ViewType
import com.nibokapp.nibok.ui.presenter.viewtype.common.InsertionSaveStatusPresenter
import com.nibokapp.nibok.ui.presenter.viewtype.common.ViewTypePresenter

/**
 * Presenter that operates on insertions of saved books.
 */
class SavedInsertionPresenter(
        override val TAG: String = SavedInsertionPresenter.TAG
) : ViewTypePresenter, InsertionSaveStatusPresenter {

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

    override fun getQueryData(query: String): List<ViewType> {
        Log.d(TAG, "Getting query saved data")
        return RequestSavedBookInsertionFromQueryCommand(query).execute()
    }
}