package com.nibokapp.nibok.ui.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.nibokapp.nibok.R
import com.nibokapp.nibok.data.repository.BookManager
import com.nibokapp.nibok.ui.activity.PublishActivity
import com.nibokapp.nibok.ui.adapter.BookAdapter
import com.nibokapp.nibok.ui.adapter.common.ViewType
import com.nibokapp.nibok.ui.fragment.common.ViewTypeFragment
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

    // Main view

    override fun getMainView(): RecyclerView = sellingBooksList

    override fun getMainViewLayoutManager() = LinearLayoutManager(context)

    override fun getMainViewAdapter() = BookAdapter()

    // Main view data

    override fun getMainViewData(): List<ViewType> = BookManager.getFeedBooksList() // TODO replace

    override fun hasMainViewUpdatableItems(): Boolean = true

    override fun hasMainViewRemovableItems(): Boolean = false

    override fun onMainViewScrollDownLoader() = Unit

    // Search view

    override fun getSearchView(): RecyclerView = getMainView() // TODO add search view and methods

    override fun getSearchViewLayoutManager() = getMainViewLayoutManager()

    override fun getSearchViewAdapter() = getMainViewAdapter()

    // Search view data

    override fun searchStrategy(query: String): List<ViewType> = emptyList() // TODO add real search



    override fun handleRefreshAction() {
        Log.i(TAG, "Refreshing")
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab.setOnClickListener {
            // TODO Check that user is logged in
            Log.d(TAG, "Open publishing activity")
            context.startActivity<PublishActivity>()
        }
    }
}