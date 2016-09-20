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
import com.nibokapp.nibok.extension.startLoginActivity
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
        val TAG: String = BaseFragment::class.java.simpleName
    }

    protected var menuSearchAction: MenuItem? = null
    protected var searchView: SearchView? = null

    private var menu: Menu? = null

    private var doLogin = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Add options menu
        setHasOptionsMenu(true)
    }

    override fun onStart() {
        super.onStart()
        updateDoLogin()
        updateAuthAction()
    }

    override fun onBecomeVisible() {
        Log.d(TAG, "${getFragmentName()} became visible")
    }

    override fun onBecomeInvisible() {
        Log.d(TAG, "${getFragmentName()} is no longer visible")
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        this.menu = menu
        inflater?.inflate(R.menu.toolbar_menu, menu)

        updateAuthAction()

        // Find the search action menu item and get the search view
        menuSearchAction = menu?.findItem(R.id.searchAction)
        searchView = menuSearchAction?.actionView as? SearchView

        // Add search hint
        searchView?.queryHint = getSearchHint()

        // Add listener to search view input
        searchView?.setOnQueryTextListener(
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

        /* Add listeners for when the search view is opened and closed.
         * This is a workaround because the setOnCloseListener method does not get invoked by Android.
         * The short way should be:
         *  searchView.setOnSearchClickListener { handleOnSearchOpen() }
         *  searchView.setOnCloseListener { handleOnSearchClose(); false }
         */
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
        if (doLogin)
            context.startLoginActivity()
        else
            doAsync {
                doLogin = authPresenter.logout()
                uiThread {
                    updateAuthAction()
                }
            }
    }

    private fun updateDoLogin() {
        Log.d(TAG, "Updating doLogin")
        doLogin = !authPresenter.loggedUserExists()
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

}


