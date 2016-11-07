package com.nibokapp.nibok.ui.fragment.main

import android.os.Bundle
import android.util.Log
import android.view.View
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.model.BookInsertionModel
import com.nibokapp.nibok.extension.getDpBasedLinearLayoutManager
import com.nibokapp.nibok.extension.startDetailActivity
import com.nibokapp.nibok.ui.activity.InsertionPublishActivity
import com.nibokapp.nibok.ui.adapter.viewtype.ViewTypeAdapter
import com.nibokapp.nibok.ui.adapter.viewtype.common.ViewType
import com.nibokapp.nibok.ui.adapter.viewtype.common.ViewTypes
import com.nibokapp.nibok.ui.fragment.main.common.ViewTypeFragment
import com.nibokapp.nibok.ui.presenter.viewtype.UserInsertionPresenter
import com.nibokapp.nibok.ui.presenter.viewtype.common.ViewTypePresenter
import kotlinx.android.synthetic.main.fragment_selling.*
import org.jetbrains.anko.startActivity

/**
 * Fragment managing the books being sold the user.
 */
class SellingFragment(
        val presenter: ViewTypePresenter = UserInsertionPresenter()
) : ViewTypeFragment() {

    companion object {
        private val TAG = SellingFragment::class.java.simpleName
    }

    // Fragment

    override fun getFragmentLayout() = R.layout.fragment_selling

    override fun getFragmentName() : String = TAG

    // Presenter

    override fun getFragmentPresenter() = presenter

    // Main view

    override fun getMainViewId() = R.id.sellingBooksList

    override fun getMainViewLayoutManager() = context.getDpBasedLinearLayoutManager()

    override fun getMainViewAdapter() = ViewTypeAdapter(bookItemClickManager)

    // Main view data

    override fun hasMainViewUpdatableItems(): Boolean = true

    override fun hasMainViewRemovableItems(): Boolean = false

    // Search view

    override fun getSearchViewId(): Int = R.id.searchResultsListSelling

    override fun getSearchViewLayoutManager() = context.getDpBasedLinearLayoutManager()

    override fun getSearchViewAdapter() = ViewTypeAdapter(bookItemClickManager)

    override fun getSearchHint() : String = getString(R.string.search_hint_book)

    // Refresh

    override fun getRefreshUnsuccessfulString(): String = getString(R.string.no_newer_book_insertions)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Wait before making sellingFab visible
        // otherwise it's initially positioned in the upper left corner
        // Appeared with version 24 of design library
        sellingFab.post { sellingFab.visibility = View.VISIBLE }
        sellingFab.setOnClickListener {
            // TODO Check that user is logged in
            Log.d(TAG, "Open publishing activity")
            context.startActivity<InsertionPublishActivity>()
        }
    }

    override fun getMainViewData() : List<ViewType> {
        val data = super.getMainViewData()
        return data.filterIsInstance<BookInsertionModel>().sortedBy { it.insertionDate }
    }

    private val bookItemClickManager = object : ViewTypeAdapter.ItemClickManager {

        override fun onButtonClick(itemId: String, itemType: Int) {}

        override fun showButton(): Boolean = false

        override fun updateItemOnButtonClick(): Boolean = false

        override fun onItemClick(itemId: String, itemType: Int) {
            if (itemType == ViewTypes.BOOK_INSERTION)
                context.startDetailActivity(itemId)
        }
    }

}