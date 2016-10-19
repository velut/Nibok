package com.nibokapp.nibok.ui.fragment.main.common

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.nibokapp.nibok.R
import com.nibokapp.nibok.extension.startAuthenticateActivity
import com.nibokapp.nibok.ui.presenter.AuthPresenter
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

/**
 * Base fragment implementing common features.
 *
 * It sets up the menu and creates stubs for handling menu actions.
 */
abstract class BaseFragment(
        val authPresenter: AuthPresenter = AuthPresenter()
) : Fragment(), VisibleFragment {

    companion object {
        private val TAG: String = BaseFragment::class.java.simpleName
    }

    private var isFragmentVisible: Boolean = false

    private lateinit var menuSearchAction: MenuItem
    private lateinit var menuSearchView: SearchView

    private var menu: Menu? = null

    // Track if the login or logout option has to be shown (true -> login; false -> logout)
    private val doLogin: Boolean
        get() = !authPresenter.loggedUserExists()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Add options menu
        setHasOptionsMenu(true)
    }

    override fun onStart() {
        super.onStart()
        updateAuthAction()
    }

    override fun onBecomeVisible() {
        Log.d(TAG, "${getFragmentName()} became visible")
        isFragmentVisible = true
    }

    override fun onBecomeInvisible() {
        if (!isFragmentVisible) return

        Log.d(TAG, "${getFragmentName()} is no longer visible")
        isFragmentVisible = false
    }

    override fun isFragmentVisible(): Boolean = isFragmentVisible

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        this.menu = menu
        inflater.inflate(R.menu.toolbar_menu, menu)

        updateAuthAction()

        // Find the search action menu item and get the search view
        menuSearchAction = menu.findItem(R.id.searchAction)
        menuSearchView = menuSearchAction.actionView as SearchView

        setupSearchView()
        addSearchActionExpandListener()
    }

    /**
     * Handle selection of options in the menu.
     *
     * @param item the selected menu item
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.searchAction -> handleSearchAction()
            R.id.refreshAction -> handleRefreshAction()
            R.id.backToTopAction -> handleBackToTopAction()
            R.id.authAction -> handleAuthAction()
            else -> handleUnknownAction(item)
        }
        return true
    }

    /**
     * Handle the search action.
     */
    open fun handleSearchAction() {
        Log.d(TAG, "Searching")
    }

    open fun handleAuthAction() {
        Log.d(TAG, "Handling auth action")
        if (doLogin) {
            context.startAuthenticateActivity()
        } else {
            doAsync {
                authPresenter.logout()
                uiThread {
                    updateAuthAction()
                }
            }
        }
    }

    private fun updateAuthAction() {
        val authItem = menu?.findItem(R.id.authAction) ?: return

        if (doLogin) {
            Log.d(TAG, "Offering login action")
            authItem.title = getString(R.string.login_action)
        } else {
            Log.d(TAG, "Offering logout action")
            authItem.title = "${getString(R.string.logout_action)} (${authPresenter.getLoggedUserId()})"
        }
    }

    /**
     * Handle the refresh action.
     */
    abstract fun handleRefreshAction()

    /**
     * Handle the back to top action.
     */
    abstract fun handleBackToTopAction()

    /**
     * Handle queries when text is submitted.
     */
    abstract fun handleOnQueryTextSubmit(query: String)

    /**
     * Handle queries when text is changed.
     */
    abstract fun handleOnQueryTextChange(query: String)

    /**
     * Handle the opening of the search.
     */
    abstract fun handleOnSearchOpen()

    /**
     * Handle the closing of the search.
     */
    abstract fun handleOnSearchClose()

    /**
     * Get the string to provide search hints.
     *
     * @return the string with the search hint
     */
    abstract fun getSearchHint() : String

    /**
     * Get the name of the actual child fragment instance of BaseFragment.
     *
     * @return the name of the fragment instance
     */
    abstract fun getFragmentName() : String

    /**
     * Handle every other action not directly handled.
     */
    private fun handleUnknownAction(item: MenuItem) {
        super.onOptionsItemSelected(item)
        context.toast("Unknown menu action")
    }

    private fun setupSearchView() {
        // Add search hint
        menuSearchView.queryHint = getSearchHint()

        // Add listener to search view input
        menuSearchView.setOnQueryTextListener(
                object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        Log.d(TAG, "Search submit: $query")
                        query?.let {
                            handleOnQueryTextSubmit(it)
                        }
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        Log.d(TAG, "Search change to: $newText")
                        newText?.let {
                            handleOnQueryTextChange(it)
                        }
                        return false
                    }
                }
        )
    }

    private fun addSearchActionExpandListener() {
        MenuItemCompat.setOnActionExpandListener(menuSearchAction,
                object : MenuItemCompat.OnActionExpandListener {
                    override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                        handleOnSearchOpen()
                        return true
                    }

                    override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                        handleOnSearchClose()
                        return true
                    }
                }
        )
    }

}


