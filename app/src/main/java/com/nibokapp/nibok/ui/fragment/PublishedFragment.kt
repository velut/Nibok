package com.nibokapp.nibok.ui.fragment

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.model.BookInsertionModel
import com.nibokapp.nibok.extension.getDpBasedLinearLayoutManager
import com.nibokapp.nibok.extension.startDetailActivity
import com.nibokapp.nibok.ui.activity.AuthenticateActivity
import com.nibokapp.nibok.ui.adapter.InsertionAdapter
import com.nibokapp.nibok.ui.presenter.main.InsertionPublishedPresenter
import com.nibokapp.nibok.ui.presenter.main.MainActivityPresenter


/**
 * MainActivityFragment used to display the insertions saved by the user.
 *
 * @param presenter the presenter used by this fragment to perform data operations
 */
class PublishedFragment(
        override val presenter: MainActivityPresenter<BookInsertionModel> = InsertionPublishedPresenter()
) : MainInsertionFragment() {

    companion object {
        private val TAG = PublishedFragment::class.java.simpleName

        private val KEY_INSERTION_TO_TOGGLE = "$TAG:insertionToToggle"
    }

    override val TAG: String = PublishedFragment.TAG

    /*
     * Layout
     */

    override val layoutId: Int = R.layout.fragment_selling

    /*
     * Presenter
     */

    // TODO delete insertion?
//    private val savePresenter: InsertionSaveStatusPresenter? by lazy {
//        presenter as? InsertionSaveStatusPresenter
//    }

    /*
     * Main view
     */

    override val mainViewId: Int = R.id.sellingBooksList

    override val mainAdapter: InsertionAdapter = InsertionAdapter(
            { context.startDetailActivity(it) },
            { context.startDetailActivity(it) },
            { toggleInsertionSaveStatus(it) } // TODO delete?
    )

    override val mainLayoutManager: LinearLayoutManager
        get() = context.getDpBasedLinearLayoutManager()


    override val mainScrollListener: RecyclerView.OnScrollListener?
        get() = buildMainViewInfiniteScrollListener()

    /*
     * Search view
     */

    override val searchViewId: Int = R.id.searchResultsListSelling

    override val searchAdapter: InsertionAdapter = InsertionAdapter(
            { context.startDetailActivity(it) },
            { context.startDetailActivity(it) },
            { TODO() }
    )

    override val searchLayoutManger: LinearLayoutManager
        get() = context.getDpBasedLinearLayoutManager()


    override val searchScrollListener: RecyclerView.OnScrollListener? = null

    override val searchHint: String by lazy { getString(R.string.search_hint_book) }

    /*
     * Fab
     */

    override var fabId: Int? = R.id.sellingFab


    override fun onSuccessfulAuthResult(data: Intent?) {
        if (data == null) return
        Log.d(TAG, "User authenticated successfully")
        val insertionId = data.getStringExtra(KEY_INSERTION_TO_TOGGLE) ?: return
//        saveInsertion(insertionId)
    }

//    private fun saveInsertion(insertionId: String) {
//        val presenter = savePresenter ?: return
//        doAsync {
//            val isSaved = presenter.isInsertionSaved(insertionId)
//            uiThread {
//                if (!isSaved) {
//                    toggleInsertionSaveStatus(insertionId)
//                }
//            }
//        }
//    }

    private fun toggleInsertionSaveStatus(insertionId: String) {

        if (!isUserLoggedIn()) {
            Log.d(TAG, "User needs to login before saving insertion")
            val intent = Intent(context, AuthenticateActivity::class.java)
            intent.putExtra(KEY_INSERTION_TO_TOGGLE, insertionId)
            startActivityForResult(intent, REQUEST_AUTHENTICATE)
            return
        }

//        Log.d(TAG, "Toggling save status for insertion: $insertionId")
//        val presenter = savePresenter ?: return
//        doAsync {
//            val isSaved = presenter.toggleInsertionSave(insertionId)
//            uiThread {
//                val items = mainAdapter.items.map {
//                    if (it.insertionId == insertionId) {
//                        it.copy(savedByUser = isSaved)
//                    } else {
//                        it
//                    }
//                }
//                mainAdapter.items = items
//            }
//        }
    }
}