package com.nibokapp.nibok.ui.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.nibokapp.nibok.R
import com.nibokapp.nibok.ui.activity.PublishActivity
import com.nibokapp.nibok.ui.adapter.ViewTypeAdapter
import com.nibokapp.nibok.ui.fragment.common.ViewTypeFragment
import com.nibokapp.nibok.ui.presenter.viewtype.InsertionFeedPresenter
import kotlinx.android.synthetic.main.fragment_selling.*
import org.jetbrains.anko.startActivity

/**
 * Fragment managing the books being sold the user.
 */
class SellingFragment : ViewTypeFragment() {

    companion object {
        private val TAG = SellingFragment::class.java.simpleName
    }

    // Fragment

    override fun getFragmentLayout() = R.layout.fragment_selling

    override fun getFragmentName() : String = TAG

    // Presenter TODO use correct presenter

    override fun getFragmentPresenter() = InsertionFeedPresenter()

    // Main view

    override fun getMainView(): RecyclerView = sellingBooksList

    override fun getMainViewId() = R.id.sellingBooksList

    override fun getMainViewLayoutManager() = LinearLayoutManager(context)

    override fun getMainViewAdapter() = ViewTypeAdapter(bookItemClickListener)

    // Main view data

    override fun hasMainViewUpdatableItems(): Boolean = true

    override fun hasMainViewRemovableItems(): Boolean = false

    override fun onMainViewScrollDownLoader() = Unit

    // Search view

    override fun getSearchView(): RecyclerView = searchResultsListSelling

    override fun getSearchViewLayoutManager() = getMainViewLayoutManager()

    override fun getSearchViewAdapter() = ViewTypeAdapter(bookItemClickListener)

    override fun getSearchHint() : String = getString(R.string.search_hint_book)


    override fun handleRefreshAction() {
        Log.i(TAG, "Refreshing")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Wait before making fab visible
        // otherwise it's initially positioned in the upper left corner
        // Appeared with version 24 of design library
        fab.post { fab.visibility = View.VISIBLE }
        fab.setOnClickListener {
            // TODO Check that user is logged in
            Log.d(TAG, "Open publishing activity")
            context.startActivity<PublishActivity>()
        }
    }

    private val bookItemClickListener = object : ViewTypeAdapter.ItemClickListener {
        override fun onButtonClick(itemId: Long, itemType: Int) {
            // TODO Implement
            Log.d(TAG, "TODO")
        }

        override fun onItemClick(itemId: Long, itemType: Int) {
            // TODO Implement
            Log.d(TAG, "TODO")
        }
    }
}