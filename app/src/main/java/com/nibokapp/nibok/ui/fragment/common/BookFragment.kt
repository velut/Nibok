package com.nibokapp.nibok.ui.fragment.common

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nibokapp.nibok.R
import com.nibokapp.nibok.data.repository.BookManager
import com.nibokapp.nibok.domain.model.BookModel
import com.nibokapp.nibok.extension.getName
import com.nibokapp.nibok.extension.inflate
import com.nibokapp.nibok.ui.adapter.common.InfiniteScrollListener
import com.nibokapp.nibok.ui.adapter.common.ListAdapter
import com.nibokapp.nibok.ui.adapter.common.ViewType

/**
 * Base fragment for fragments representing books lists.
 */
abstract class BookFragment : BaseFragment() {

    companion object {
        private val TAG = BookFragment::class.java.simpleName
    }

    private var oldQuery: String? = null
    private var oldResults: List<BookModel>? = null

    private var listView: RecyclerView? = null
    private var searchResultsView: RecyclerView? = null
    private var currentView: RecyclerView? = null

    /**
     * Get the layout used by the fragment.
     *
     * @return the fragment's layout id
     */
    abstract fun getFragmentLayout() : Int

    /**
     * Get the layout manager used by the books view.
     *
     * @return the layout manager used by the books view
     */
    abstract fun getBooksViewLayoutManager() : LinearLayoutManager

    /**
     * Get the books view defined in the fragment's layout.
     *
     * @return the books view defined in the fragment's layout.
     */
    abstract fun getBooksView() : RecyclerView

    /**
     * Get the adapter used by the books view.
     *
     * @return the adapter used by the books view
     */
    abstract fun getBooksViewAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>

    /**
     * Get the layout manager used by the search view.
     *
     * @return the layout manager used by the search view
     */
    abstract fun getSearchViewLayoutManager() : LinearLayoutManager

    /**
     * Get the search view defined in the fragment's layout.
     *
     * @return the search view defined in the fragment's layout.
     */
    abstract fun getSearchView() : RecyclerView

    /**
     * Get the adapter used by the search view.
     *
     * @return the adapter used by the search view
     */
    abstract fun getSearchViewAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>

    /**
     * The loading function called when scrolling down the books view.
     */
    abstract fun onScrollDownLoader()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(getFragmentLayout())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupBooksView()
        listView = getBooksView()
    }

    override fun onBecomeVisible() {
        super.onBecomeVisible()
        showListView()
    }

    /**
     * Scroll back to the top of the books view.
     */
    override fun handleBackToTopAction() {
        currentView?.let {
            Log.d(TAG, "Going back to top")
            it.layoutManager.scrollToPosition(0)
        }
    }

    override fun handleOnQueryTextSubmit(query: String) = handleOnQueryTextChange(query)

    override fun handleOnQueryTextChange(query: String) {
        if (query.equals(oldQuery)) {
            Log.d(TAG, "Same query as before, return")
            return
        }
        oldQuery = query

        val results = BookManager.getBooksFromQuery(query)
        if (results.equals(oldResults)) {
            Log.d(TAG, "Same results as before, return")
            return
        }
        oldResults = results

        @Suppress("UNCHECKED_CAST")
        val adapter = getSearchView().adapter as? ListAdapter<ViewType>
        adapter?.let {
            it.clearAndAddItems(results)
        }
    }

    override fun handleOnSearchOpen() {
        Log.d(TAG, "Search opened. Hide BooksView and show SearchView")
        listView?.visibility = View.GONE
        setupSearchView()
        searchResultsView = getSearchView()
        searchResultsView?.visibility = View.VISIBLE
        currentView = searchResultsView
    }

    override fun handleOnSearchClose() {
        Log.d(TAG, "Search closed. Hide SearchView and show BooksView")
        showListView()
    }

    override fun getSearchHint() : String = getString(R.string.search_hint_book)

    /**
     * Initial setup of a recycler view.
     * Assign layout manager and adapter, add infinite scroll listener if requested.
     *
     * @param view the recycler view to setup
     * @param viewLM the linear layout manager of the view
     * @param viewAdapter the adapter for the view
     * @param hasCustomScrollListener true if a custom scroll listener has to be added
     */
    private fun setupView(view: RecyclerView,
                          viewLM: LinearLayoutManager,
                          viewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
                          hasCustomScrollListener: Boolean) {

        val viewName = view.getName()
        Log.d(TAG, "Setting up View: $viewName")
        view.apply {
            // Performance improvement
            setHasFixedSize(true)
            // Assign layout manager
            layoutManager = viewLM
            // Assign adapter
            if (adapter == null) {
                adapter = viewAdapter
            }
            if (hasCustomScrollListener) {
                // Add infinite scroll listener
                clearOnScrollListeners()
                addOnScrollListener(InfiniteScrollListener(viewLM) {
                    // Custom loading function executed on scroll down
                    onScrollDownLoader()
                })
            }
        }
    }

    private fun setupBooksView() =
            setupView(getBooksView(), getBooksViewLayoutManager(), getBooksViewAdapter(), true)

    private fun setupSearchView() =
            setupView(getSearchView(), getSearchViewLayoutManager(), getSearchViewAdapter(), false)

    /**
     * Hide search results view (if present) and show list view (if present)
     */
    private fun showListView() {
        searchResultsView?.visibility = View.GONE
        listView?.visibility = View.VISIBLE
        currentView = listView
    }
}