package com.nibokapp.nibok.ui.fragment.main.common

import android.os.Bundle
import android.os.Handler
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nibokapp.nibok.R
import com.nibokapp.nibok.extension.getName
import com.nibokapp.nibok.extension.inflate
import com.nibokapp.nibok.ui.adapter.viewtype.common.InfiniteScrollListener
import com.nibokapp.nibok.ui.adapter.viewtype.common.ListAdapter
import com.nibokapp.nibok.ui.adapter.viewtype.common.ViewType
import com.nibokapp.nibok.ui.presenter.viewtype.common.ViewTypePresenter
import org.jetbrains.anko.*

/**
 * Base fragment for fragments representing ViewTypes.
 *
 * It handles the creation of the main view and of the search view and querying.
 */
abstract class ViewTypeFragment : BaseFragment() {

    companion object {
        private val TAG = ViewTypeFragment::class.java.simpleName
    }

    private var isFragmentVisible = false

    private var oldQuery: String? = null
    private var oldResults: List<ViewType> = emptyList()

    private val presenter: ViewTypePresenter by lazy { getFragmentPresenter() }

    private var mainView: RecyclerView? = null
    private var searchResultsView: RecyclerView? = null
    private var currentView: RecyclerView? = null
    private var fab: FloatingActionButton? = null

    /**
     * Get the layout used by the fragment.
     *
     * @return the fragment's layout id
     */
    abstract fun getFragmentLayout() : Int

    /**
     * Get the presenter used by the fragment.
     *
     * @return the fragment's presenter
     */
    abstract fun getFragmentPresenter() : ViewTypePresenter

    /**
     * Get the main view defined in the fragment's layout.
     *
     * @return the main view defined in the fragment's layout.
     */
    abstract fun getMainView() : RecyclerView

    /**
     * Get the id of the main view.
     *
     * @return the id of the main view
     */
    abstract fun getMainViewId() : Int

    /**
     * Get the layout manager used by the main view.
     *
     * @return the layout manager used by the main view
     */
    abstract fun getMainViewLayoutManager() : LinearLayoutManager

    /**
     * Get the adapter used by the main view.
     *
     * @return the adapter used by the main view
     */
    abstract fun getMainViewAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>

    /**
     * Get the search view defined in the fragment's layout.
     *
     * @return the search view defined in the fragment's layout.
     */
    abstract fun getSearchView() : RecyclerView

    /**
     * Get the layout manager used by the search view.
     *
     * @return the layout manager used by the search view
     */
    abstract fun getSearchViewLayoutManager() : LinearLayoutManager

    /**
     * Get the adapter used by the search view.
     *
     * @return the adapter used by the search view
     */
    abstract fun getSearchViewAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>

    /**
     * Signal if items in the main view can be updated.
     *
     * @return true if the items can be updated, false otherwise
     */
    abstract fun hasMainViewUpdatableItems() : Boolean

    /**
     * Signal if items in the main view can be deleted.
     *
     * @return true if the items can be deleted, false otherwise
     */
    abstract fun hasMainViewRemovableItems() : Boolean

    abstract fun getNoNewerItemsFromRefreshString() : String


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = container?.inflate(getFragmentLayout())
        view?.let {
            val mainView = it.find<RecyclerView>(getMainViewId())
            setupMainView(mainView)
            // Set cached items to restore scroll position
            getAdapterForView(mainView)?.clearAndAddItems(presenter.getCachedData())
            currentView = mainView
            fab = it.findOptional(R.id.sellingFab)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainView = getMainView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        checkForUpdates()
    }

    override fun onBecomeVisible() {
        super.onBecomeVisible()
        isFragmentVisible = true
        showMainView()
        checkForUpdates()
    }

    override fun onBecomeInvisible() {
        if (isFragmentVisible) {
            isFragmentVisible = false
            super.onBecomeInvisible()
        }
    }

    /**
     * Scroll back to the top of the current view.
     */
    override fun handleBackToTopAction() {
        currentView?.let {
            Log.d(TAG, "Going back to top")
            it.layoutManager.scrollToPosition(0)
        }
    }

    override fun handleRefreshAction() {

        val mainViewAdapter = getAdapterForView(mainView)
        if (mainViewAdapter == null) {
            Log.d(TAG, "Main view adapter is null, cannot refresh")
            return
        }

        val numAdded : Int

        val firstItem = getMainViewData().firstOrNull()
        if (firstItem == null) {
            numAdded = mainViewAdapter.addItems(presenter.getData())
        } else {
            val newerData = presenter.getDataNewerThanItem(firstItem)
            numAdded = mainViewAdapter.addItems(newerData)
        }

        if (numAdded == 0) {
            context.toast(getNoNewerItemsFromRefreshString())
        } else {
            handleBackToTopAction()
        }
    }

    override fun handleOnQueryTextSubmit(query: String) = handleOnQueryTextChange(query)

    override fun handleOnQueryTextChange(query: String) {
        if (query.equals(oldQuery)) {
            Log.d(TAG, "Same query as before, return")
            return
        }
        oldQuery = query

        val results = presenter.getQueryData(query)
        Log.d(TAG, "Results size: ${results.size}")

        if (results.equals(oldResults)) {
            Log.d(TAG, "Same results as before, return")
            return
        }
        oldResults = results

        getAdapterForView(searchResultsView)?.clearAndAddItems(results)
    }

    override fun handleOnSearchOpen() {
        Log.d(TAG, "Search opened. Hide MainView and show SearchView")
        mainView?.visibility = View.GONE
        fab?.visibility = View.GONE
        setupSearchView()
        searchResultsView = getSearchView()
        searchResultsView?.visibility = View.VISIBLE
        // Update the current view to be the search results view
        currentView = searchResultsView
    }

    override fun handleOnSearchClose() {
        Log.d(TAG, "Search closed. Hide SearchView and show MainView")
        oldQuery = null
        oldResults = emptyList()
        onBecomeVisible()
    }

    /**
     * Check asynchronously for item updates that happened since the last retrieval
     * of the main view data.
     *
     * If there are updates to be applied then check what needs to be removed and what needs to
     * be updated and inform accordingly in the ui thread the main view adapter.
     */
    fun checkForUpdates() {

        val viewAdapter = getAdapterForView(mainView)
        if (viewAdapter == null) {
            Log.d(TAG, "No adapter for main view, cannot update")
            return
        }
        Log.d(TAG, "${getFragmentName()} is checking for updates")

        doAsync() {
            val oldData = viewAdapter.getItems()
            val (toUpdate, toRemove) = presenter.getDiffData(oldData)

            Log.d(TAG, "${getFragmentName()}: Update: ${toUpdate.size}; Remove: ${toRemove.size}")

            if (hasMainViewRemovableItems()) {
                Log.d(TAG, "Items to remove from ${getFragmentName()}: ${toRemove.size}")
                if (toRemove.size > 0) uiThread { viewAdapter.removeItems(toRemove) }
            }

            if (hasMainViewUpdatableItems()) {
                Log.d(TAG, "Items to update in ${getFragmentName()}: ${toUpdate.size}")
                if (toUpdate.size > 0) uiThread { viewAdapter.updateItems(toUpdate) }
            }
        }
    }

    /**
     * Get the data currently displayed in the main view by the
     * main view adapter.
     *
     * @return the data currently displayed in the main view
     */
    open fun getMainViewData() : List<ViewType> =
            getAdapterForView(mainView)?.getItems() ?: emptyList()

    private fun getAdapterForView(view: RecyclerView?) : ListAdapter<ViewType>? {
        @Suppress("UNCHECKED_CAST")
        return view?.adapter as? ListAdapter<ViewType>
    }

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
            adapter = viewAdapter
            if (hasCustomScrollListener) {
                // Add infinite scroll listener
                clearOnScrollListeners()
                addOnScrollListener(InfiniteScrollListener(viewLM) {
                    // Custom loading function executed on scroll down
                    loadMoreItems()
                })
            }
        }
    }

    private fun loadMoreItems() {
        val mainViewAdapter = getAdapterForView(mainView)

        if (mainViewAdapter == null) {
            Log.d(TAG, "No adapter for main view, cannot load more items")
            return
        }

        Log.d(TAG, "Loading more items")

        var numAdded : Int
        val handler = Handler()

        val lastItem = getMainViewData().lastOrNull()

        handler.post {
            if (lastItem == null) {
                numAdded = mainViewAdapter.addItems(presenter.getData())
            } else {
                val olderData = presenter.getDataOlderThanItem(lastItem)
                numAdded = mainViewAdapter.addItems(olderData, insertAtBottom = true)
            }

            if (numAdded == 0) {
                context.toast(getString(R.string.end_reached))
            }
        }
    }

    private fun setupMainView(view: RecyclerView) =
            setupView(view, getMainViewLayoutManager(), getMainViewAdapter(), true)

    private fun setupSearchView() =
            setupView(getSearchView(), getSearchViewLayoutManager(), getSearchViewAdapter(), false)

    /**
     * Hide search results view (if present) and show main view (if present).
     * Update currentView to be the mainView.
     */
    private fun showMainView() {
        searchResultsView?.visibility = View.GONE
        mainView?.visibility = View.VISIBLE
        fab?.let {
            it.post { it.visibility = View.VISIBLE }
        }
        currentView = mainView
    }
}