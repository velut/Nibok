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
import com.nibokapp.nibok.ui.adapter.viewtype.ViewTypeAdapter
import com.nibokapp.nibok.ui.adapter.viewtype.common.ViewType
import com.nibokapp.nibok.ui.behavior.InfiniteScrollListener
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

    /**
     * Properties tracking the old query input and the old result list
     * in order to prevent unnecessary updates to the searchView.
     */
    private var oldQuery: String? = null
    private var oldResults: List<ViewType> = emptyList()

    /**
     * The presenter used to retrieve and operate on data.
     */
    private val presenter: ViewTypePresenter by lazy { getFragmentPresenter() }

    /**
     * The main view, that is the default recycler view
     * in which the fragment's items are displayed, and its adapter.
     */
    private lateinit var mainView: RecyclerView
    private lateinit var mainViewAdapter: ViewTypeAdapter

    /**
     * The search view, that is the recycler view (hidden by default)
     * in which the items returned from a search are displayed, and its adapter.
     */
    private lateinit var searchView: RecyclerView
    private lateinit var searchViewAdapter: ViewTypeAdapter

    /**
     * Track the view that is currently displayed.
     */
    private var currentView: RecyclerView? = null

    /**
     * Reference to the optional fab that a fragment may be displaying.
     */
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
    abstract fun getMainViewAdapter() : ViewTypeAdapter

    /**
     * Get the id of the search view.
     *
     * @return the id of the search view
     */
    abstract fun getSearchViewId() : Int

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
    abstract fun getSearchViewAdapter() : ViewTypeAdapter

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

    abstract fun getRefreshUnsuccessfulString() : String


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = container?.inflate(getFragmentLayout())
        view?.let {
            // Find the views
            mainView = it.find<RecyclerView>(getMainViewId())
            searchView = it.find(getSearchViewId())
            fab = it.findOptional(R.id.sellingFab)

            // Initially the current view is the main view
            currentView = mainView

            // Initialize the main and the search view
            initMainView()
            initSearchView()
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG, "Activity created, checking for updates")
        checkForUpdates()
    }

    override fun onBecomeVisible() {
        super.onBecomeVisible()
        showMainView()
        checkForUpdates()
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
        val firstItem = getMainViewData().firstOrNull()
        doAsync {
            val data = if (firstItem == null)
                presenter.getData()
            else
                presenter.getDataNewerThanItem(firstItem)
            uiThread {
                val numAdded = mainViewAdapter.addItems(data)
                onRefreshed(numAdded)
            }
        }
    }

    override fun handleOnQueryTextSubmit(query: String) = handleOnQueryTextChange(query)

    override fun handleOnQueryTextChange(query: String) {
        if (query == oldQuery) {
            Log.d(TAG, "Same query as before, return")
            return
        }
        oldQuery = query
        doAsync {
            val results = presenter.getQueryData(query)
            Log.d(TAG, "Results size: ${results.size}")

            if (results == oldResults) {
                Log.d(TAG, "Same results as before, return")
                return@doAsync
            }
            oldResults = results
            uiThread {
                searchViewAdapter.clearAndAddItems(results)
            }
        }
    }

    override fun handleOnSearchOpen() {
        Log.d(TAG, "Search opened. Hide MainView and show SearchView")
        mainView.visibility = View.GONE
        fab?.visibility = View.GONE

        searchView.visibility = View.VISIBLE
        // Update the current view to be the search results view
        currentView = searchView
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

        Log.d(TAG, "${getFragmentName()} is checking for updates")

        doAsync() {
            val oldData = mainViewAdapter.getItems()
            val (toUpdate, toRemove) = presenter.getDiffData(oldData)

            Log.d(TAG, "${getFragmentName()}: Update: ${toUpdate.size}; Remove: ${toRemove.size}")

            if (hasMainViewRemovableItems()) {
                Log.d(TAG, "Items to remove from ${getFragmentName()}: ${toRemove.size}")
                if (toRemove.size > 0) uiThread { mainViewAdapter.removeItems(toRemove) }
            }

            if (hasMainViewUpdatableItems()) {
                Log.d(TAG, "Items to update in ${getFragmentName()}: ${toUpdate.size}")
                if (toUpdate.size > 0) uiThread { mainViewAdapter.updateItems(toUpdate) }
            }
        }
    }

    private fun onRefreshed(numAdded: Int) {
        if (numAdded == 0) {
            context.toast(getRefreshUnsuccessfulString())
        } else {
            handleBackToTopAction()
        }
    }

    /**
     * Get the data currently displayed in the main view by the
     * main view adapter.
     *
     * @return the data currently displayed in the main view
     */
    open fun getMainViewData() : List<ViewType> = mainViewAdapter.getItems()

    private fun initMainView() {
        setupMainView()
        // Set cached items to restore scroll position
        Log.d(TAG, "Setting up view with cached data")
        mainViewAdapter.clearAndAddItems(presenter.getCachedData())
    }

    private fun initSearchView() {
        setupSearchView()
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
        Log.d(TAG, "Loading more items")

        val handler = Handler()

        val lastItem = getMainViewData().lastOrNull()

        handler.post {
            val numAdded = if (lastItem == null) {
                mainViewAdapter.addItems(presenter.getData())
            } else {
                val olderData = presenter.getDataOlderThanItem(lastItem)
                mainViewAdapter.addItems(olderData, insertAtBottom = true)
            }

            if (numAdded == 0) {
                context.toast(getString(R.string.end_reached))
            }
        }
    }

    private fun setupMainView() {
        mainViewAdapter = getMainViewAdapter()
        setupView(mainView, getMainViewLayoutManager(), mainViewAdapter, true)
    }

    private fun setupSearchView() {
        searchViewAdapter = getSearchViewAdapter()
        setupView(searchView, getSearchViewLayoutManager(), searchViewAdapter, false)
    }

    /**
     * Hide search results view (if present) and show main view (if present).
     * Update currentView to be the mainView.
     */
    private fun showMainView() {
        searchView.visibility = View.GONE
        mainView.visibility = View.VISIBLE
        fab?.let {
            it.post { it.visibility = View.VISIBLE }
        }
        currentView = mainView
    }
}