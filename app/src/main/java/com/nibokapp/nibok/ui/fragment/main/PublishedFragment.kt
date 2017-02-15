package com.nibokapp.nibok.ui.fragment.main

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.model.BookInsertionModel
import com.nibokapp.nibok.extension.getDpBasedLinearLayoutManager
import com.nibokapp.nibok.extension.startDetailActivity
import com.nibokapp.nibok.ui.activity.AuthenticateActivity
import com.nibokapp.nibok.ui.activity.InsertionPublishActivity
import com.nibokapp.nibok.ui.adapter.main.InsertionAdapter
import com.nibokapp.nibok.ui.adapter.main.UpdatableAdapter
import com.nibokapp.nibok.ui.fragment.main.common.MainUpdatableAdapterFragment
import com.nibokapp.nibok.ui.presenter.main.InsertionDeletePresenter
import com.nibokapp.nibok.ui.presenter.main.InsertionPublishedPresenter
import com.nibokapp.nibok.ui.presenter.main.MainActivityPresenter
import kotlinx.android.synthetic.main.fragment_published.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread


/**
 * MainActivityFragment used to display the insertions saved by the user.
 *
 * @param presenter the presenter used by this fragment to perform data operations
 */
class PublishedFragment(
        override val presenter: MainActivityPresenter<BookInsertionModel> = InsertionPublishedPresenter()
) : MainUpdatableAdapterFragment<BookInsertionModel>() {

    companion object {
        private val TAG = PublishedFragment::class.java.simpleName
    }

    override val TAG: String = Companion.TAG

    /*
     * Layout
     */

    override val layoutId: Int = R.layout.fragment_published

    /*
     * Presenter
     */

    private val deletePresenter: InsertionDeletePresenter? by lazy {
        presenter as? InsertionDeletePresenter
    }

    /*
     * Main view
     */

    override val mainViewId: Int = R.id.publishedList

    override val mainAdapter: InsertionAdapter = InsertionAdapter(
            { context.startDetailActivity(it) },
            { context.startDetailActivity(it) },
            onDeleteButtonClick = { confirmDeleteInsertion(it) }
    )

    override val mainLayoutManager: LinearLayoutManager
        get() = context.getDpBasedLinearLayoutManager()


    override val mainScrollListener: RecyclerView.OnScrollListener?
        get() = buildMainViewInfiniteScrollListener()

    /*
     * Search view
     */

    override val searchViewId: Int = R.id.searchPublishedList

    override val searchAdapter: InsertionAdapter = InsertionAdapter(
            { context.startDetailActivity(it) },
            { context.startDetailActivity(it) },
            onDeleteButtonClick = { confirmDeleteInsertion(it) }
    )

    override val searchLayoutManger: LinearLayoutManager
        get() = context.getDpBasedLinearLayoutManager()


    override val searchScrollListener: RecyclerView.OnScrollListener? = null

    override val searchHint: String by lazy { getString(R.string.search_hint_book) }

    override val noSearchResultsHintId: Int = R.string.search_hint_no_published_insertions

    /*
     * Updatable adapters
     */

    override val mainUpdatableAdapter: UpdatableAdapter<BookInsertionModel>? = mainAdapter

    override val searchUpdatableAdapter: UpdatableAdapter<BookInsertionModel>? = searchAdapter

    /*
     * Fab
     */

    override var fabId: Int? = R.id.publishFab

    /*
     * Dialogs
     */

    private var alertDialog: MaterialDialog? = null

    private var resultDialog: MaterialDialog? = null


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addFabDisplayScrollListener()
        addFabClickListener()
    }

    override fun onPause() {
        super.onPause()
        alertDialog?.dismiss()
        resultDialog?.dismiss()
    }

    override fun onSuccessfulAuthResult(data: Intent?) {
        super.onSuccessfulAuthResult(data)
        Log.d(TAG, "User authenticated successfully")
        startInsertionPublishActivity()
    }

    private fun addFabDisplayScrollListener() {
        mainView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                if (dy > 0) { // Scroll down, hide fab
                    fab?.hide()
                } else if (dy < 0) { // Scroll up, show fab
                    fab?.show()
                }
            }
        })
    }

    private fun addFabClickListener() {
        publishFab.setOnClickListener {
            if (!isUserLoggedIn()) {
                Log.d(TAG, "User needs to login before publishing an insertion")
                val intent = Intent(context, AuthenticateActivity::class.java)
                startActivityForResult(intent, REQUEST_AUTHENTICATE)
            } else {
                startInsertionPublishActivity()
            }
        }
    }

    private fun startInsertionPublishActivity() {
        Log.d(TAG, "Starting insertion publishing activity")
        context.startActivity<InsertionPublishActivity>()
    }

    private fun confirmDeleteInsertion(insertionId: String) {
        if (!isUserLoggedIn()) return

        val presenter = deletePresenter ?: return
        alertDialog = MaterialDialog.Builder(context)
                .title(R.string.title_delete_insertion)
                .content(R.string.content_delete_insertion)
                .positiveText(R.string.positive_delete)
                .negativeText(R.string.text_cancel)
                .onPositive {
                    materialDialog, dialogAction -> deleteInsertion(presenter, insertionId)
                }
                .build()
        alertDialog?.show()
    }

    private fun deleteInsertion(presenter: InsertionDeletePresenter, insertionId: String) {
        Log.d(TAG, "Deleting insertion: $insertionId")
        doAsync {
            val isDeleted = presenter.deleteInsertion(insertionId)
            uiThread {
                if (isDeleted) {
                    mainAdapter.removeInsertion(insertionId)
                    searchAdapter.removeInsertion(insertionId)
                }
                showDeleteResult(isDeleted)
                if (mainAdapter.items.isEmpty() && isMainViewVisible()) {
                    showPlaceholderView()
                }
            }
        }
    }

    private fun showDeleteResult(isDeleted: Boolean) {
        val (title, content) = if (isDeleted) {
            Pair(R.string.title_delete_success, R.string.content_delete_success)
        } else {
            Pair(R.string.title_delete_fail, R.string.content_delete_fail)
        }
        resultDialog = MaterialDialog.Builder(context)
                .title(title)
                .content(content)
                .positiveText(android.R.string.ok)
                .build()
        resultDialog?.show()
    }

}