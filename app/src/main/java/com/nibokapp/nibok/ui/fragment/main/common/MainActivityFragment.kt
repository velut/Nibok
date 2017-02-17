package com.nibokapp.nibok.ui.fragment.main.common

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import com.nibokapp.nibok.R
import com.nibokapp.nibok.extension.*
import com.nibokapp.nibok.ui.presenter.authentication.AuthPresenter
import org.jetbrains.anko.*

/**
 * Abstract class at the base of fragments displayed in the main activity's viewpager.
 *
 * @param authPresenter the authentication presenter used to handle user authentication.
 *                      Default one is AuthPresenter.
 */
abstract class MainActivityFragment(
        val authPresenter: AuthPresenter = AuthPresenter()
) : Fragment(), ViewPagerFragment {

    companion object {
        private val P_TAG: String = MainActivityFragment::class.java.simpleName

        val REQUEST_AUTHENTICATE = 1

        private val KEY_IS_MAIN_VIEW_VISIBLE = "$P_TAG:KEY_IS_MAIN_VIEW_VISIBLE"
        private val KEY_SEARCH_VIEW_TEXT = "$P_TAG:KEY_SEARCH_VIEW_TEXT"
    }

    /**
     * TAG for log output.
     */
    protected abstract val TAG: String

    /**
     * Layout's id for the fragment.
     */
    protected abstract val layoutId: Int

    /**
     * Id of the main recycler view.
     */
    protected abstract val mainViewId: Int

    /**
     * Main view is the RecyclerView primarily displayed in the fragment.
     */
    protected var mainView: RecyclerView? = null

    /**
     * Adapter for the main recycler view.
     */
    protected abstract val mainAdapter: RecyclerView.Adapter<*>

    /**
     * Layout manager for the main recycler view.
     */
    protected abstract val mainLayoutManager: LinearLayoutManager

    /**
     * Scroll listener for the main recycler view.
     */
    protected abstract val mainScrollListener: RecyclerView.OnScrollListener?

    /**
     * Id of the search recycler view.
     */
    protected abstract val searchViewId: Int

    /**
     * Search view is the RecyclerView used to display search results in the fragment.
     */
    protected var searchView: RecyclerView? = null

    /**
     * Adapter for the search recycler view.
     */
    protected abstract val searchAdapter: RecyclerView.Adapter<*>

    /**
     * Layout manager for the search recycler view.
     */
    protected abstract val searchLayoutManger: LinearLayoutManager

    /**
     * Scroll listener for the search recycler view.
     */
    protected abstract val searchScrollListener: RecyclerView.OnScrollListener?

    /**
     * Hint displayed in the toolbar when searching.
     */
    protected abstract val searchHint: String

    /**
     * Hint displayed in the search TextView when no results are found.
     */
    protected abstract val noSearchResultsHintId: Int

    /**
     * Id of the FAB.
     * If not null the fragment will try to find the fab in the view.
     */
    protected open var fabId: Int? = null

    /**
     * Optional FAB displayed in the view.
     */
    protected var fab: FloatingActionButton? = null

    /**
     * Optional progress bar displayed when loading data.
     */
    protected var progressBar: ProgressBar? = null

    /**
     * TextView displayed when no results are found for a given query submitted in the search view.
     */
    protected var noSearchResultsView: TextView? = null

    /**
     * Placeholder view displayed when no items are shown in the fragment.
     */
    protected var placeholderView: View? = null

    /**
     * Current view tracks which view between mainView and searchView
     * is currently displayed in the fragment.
     */
    private var currentView: RecyclerView? = null

    /**
     * Track the latest query input in the searchActionView.
     */
    private var searchViewText: String = ""

    /**
     * Fill the adapter with cached data.
     */
    abstract fun addCachedData(): Unit

    /**
     * Fill the adapter with fresh data.
     */
    abstract fun updateData(): Unit

    /**
     * Handle submission of a query.
     *
     * @param query the String containing the query
     *
     * @return true if the query was handled, false if the SearchView should handle it
     */
    abstract fun onQueryTextSubmit(query: String): Boolean

    /**
     * Handle text change from a query.
     *
     * @param newText the String representing the new query's text
     *
     * @return true if the query was handled, false if the SearchView should handle it
     */
    abstract fun onQueryTextChange(newText: String): Boolean

    /**
     * Handle a successful authentication.
     *
     * @param data the data received from onActivityResult()
     */
    open fun onSuccessfulAuthResult(data: Intent?) {
    }

    /**
     * Handle logout.
     */
    open fun onLogout() {
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = container?.inflate(layoutId)
        view?.let {
            v ->
            mainView = v.find(mainViewId)
            searchView = v.find(searchViewId)
            fabId?.let { fab = v.findOptional(it) }
            progressBar = v.findOptional(R.id.progressBar)
            noSearchResultsView = v.findOptional(R.id.search_no_results)
            placeholderView = v.findOptional(R.id.placeholderView)
        }
        mainView?.let {
            setupRecyclerView(it, mainLayoutManager, mainAdapter, { mainScrollListener })
        }
        addCachedData()
        searchView?.let {
            setupRecyclerView(it, searchLayoutManger, searchAdapter, { searchScrollListener })
        }
        currentView = mainView
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var isMainViewVisible = true
        savedInstanceState?.let {
            isMainViewVisible = it.getBoolean(KEY_IS_MAIN_VIEW_VISIBLE, true)
            searchViewText = it.getString(KEY_SEARCH_VIEW_TEXT, "")
        }
        Log.d(TAG, "Restored: vis: $isMainViewVisible; search: $searchViewText")
        if (isMainViewVisible) {
            showMainView()
        } else {
            // When currentView == searchView after config change
            // in onCreateOptionsMenu the searchView will be restored
            currentView = searchView
        }
    }

    override fun onStart() {
        super.onStart()
        updateData()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_menu, menu)
        val searchActionItem = menu.findItem(R.id.searchAction) ?: return
        val searchActionView = searchActionItem.actionView as? SearchView ?: return
        searchActionItem.addExpandListener(
                { onSearchOpen() },
                { onSearchClose() }
        )
        searchActionView.apply {
            queryHint = searchHint
            onQueryListener(
                    { searchViewText = it; onQueryTextSubmit(it) },
                    { searchViewText = it; onQueryTextChange(it)}
            )
        }
        if (currentView == searchView) {
            hideNoResultsView()
            val oldQuery = searchViewText
            Log.d(TAG, "Restoring search view for query: $oldQuery")
            searchActionItem.expandActionView()
            searchActionView.apply {
                setQuery(oldQuery, true) // Add old query and execute it
                clearFocus() // Hide the keyboard
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val authItem = menu.findItem(R.id.authAction) ?: return
        val authTitle = if (authPresenter.loggedUserExists()) {
            Log.d(TAG, "Offering logout option")
            val logout = getString(R.string.logout_action)
            val username = authPresenter.getLoggedUserId()
            "$logout($username)"
        } else {
            Log.d(TAG, "Offering login option")
            getString(R.string.login_action)
        }
        authItem.title = authTitle
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.searchAction -> { Log.d(TAG, "Searching") }
            R.id.refreshAction -> updateData()
            R.id.backToTopAction -> {
                currentView?.layoutManager?.scrollToPosition(0); showFab()
            }
            R.id.authAction -> onAuthItemSelected()
            else -> { context.toast(R.string.error_unknown_menu_action); super.onOptionsItemSelected(item) }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_AUTHENTICATE) {
            if (resultCode == RESULT_OK) {
                onSuccessfulAuthResult(data)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val mv = mainView ?: return
        Log.d(TAG, "Saving: vis: ${mv.isVisible()}; search: $searchViewText")
        outState.apply {
            putBoolean(KEY_IS_MAIN_VIEW_VISIBLE, mv.isVisible())
            putString(KEY_SEARCH_VIEW_TEXT, searchViewText)
        }
    }

    override fun onSelected() {
        // When the fragment becomes visible in the viewpager trigger a data update
        updateData()
    }

    override fun onUnselected() {
        // If the user is changing fragment close the search view
        // and revert to the main view
        onSearchClose()
    }

    protected fun isUserLoggedIn(): Boolean = authPresenter.loggedUserExists()

    private fun setupRecyclerView(rv: RecyclerView,
                                  rvLM: RecyclerView.LayoutManager,
                                  rvAdapter: RecyclerView.Adapter<*>,
                                  onScrollListenerBuilder: () -> RecyclerView.OnScrollListener?) {
        Log.d(TAG, "Setting up view: ${rv.getName()}")
        rv.apply {
            setHasFixedSize(true)
            layoutManager = rvLM
            adapter = rvAdapter
            val onScrollListener = onScrollListenerBuilder()
            onScrollListener?.let {
                clearOnScrollListeners()
                Log.d(TAG, "Adding scroll listener")
                addOnScrollListener(it)
            }
        }
    }

    private fun onSearchOpen(): Boolean {
        Log.d(TAG, "Menu search opened")
        showSearchView()
        return true // Expand view
    }

    private fun showSearchView() {
        Log.d(TAG, "Show search view")
        mainView?.setGone()
        hidePlaceholderView()
        hideFab()
        searchView?.setVisible()
        showProgressBar()
        currentView = searchView
    }

    protected open fun onSearchClose(): Boolean {
        Log.d(TAG, "Menu search closed")
        showMainView()
        return true // Collapse view
    }

    private fun showMainView() {
        Log.d(TAG, "Show main view")
        hideProgressBar()
        searchView?.setGone()
        mainView?.setVisible()
        val mainAdapterIsEmpty = mainAdapter.itemCount == 0
        if (mainAdapterIsEmpty) {
            showPlaceholderView()
        }
        showFab()
        currentView = mainView
    }

    private fun onAuthItemSelected() {
        if (authPresenter.loggedUserExists()) {
            Log.d(TAG, "User wants to logout")
            doAsync {
                val loggedOut = authPresenter.logout()
                uiThread {
                    if (loggedOut) {
                        onLogout()
                    }
                }
            }
        } else {
            context.startAuthenticateActivity()
        }
    }

    protected fun isMainViewVisible(): Boolean {
        return mainView?.isVisible() ?: false
    }

    protected fun isSearchViewVisible(): Boolean {
        return searchView?.isVisible() ?: false
    }

    private fun showFab() {
        fab?.let { it.post { it.show() } }
    }

    private fun hideFab() {
        fab?.let { it.post { it.hide() } }
    }

    /**
     * Show the progress bar, if any.
     */
    protected fun showProgressBar() {
        Log.d(TAG, "Showing progress bar")
        progressBar?.setVisible()
    }

    /**
     * Hide the progress bar, if any.
     */
    protected fun hideProgressBar() {
        Log.d(TAG, "Hiding progress bar")
        progressBar?.setGone()
    }

    /**
     * Show the text view, if any, signaling that no results where found while searching.
     *
     * @param text the text to display
     */
    protected fun showNoResultsView(text: String) {
        Log.d(TAG, "Showing no results text view")
        noSearchResultsView?.text = text
        noSearchResultsView?.setVisible()
    }

    /**
     * Hide the text view, if any, displayed when no results where found while searching.
     */
    protected fun hideNoResultsView() {
        Log.d(TAG, "Hiding no results text view")
        noSearchResultsView?.setGone()
    }

    /**
     * Show the placeholder view, if any.
     */
    protected fun showPlaceholderView() {
        Log.d(TAG, "Showing placeholder view")
        placeholderView?.setVisible()
    }

    /**
     * Hide the placeholder view, if any.
     */
    protected fun hidePlaceholderView() {
        Log.d(TAG, "Hiding placeholder view")
        placeholderView?.setGone()
    }

}
