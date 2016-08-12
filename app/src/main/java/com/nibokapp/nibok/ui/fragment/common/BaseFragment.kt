package com.nibokapp.nibok.ui.fragment.common

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.nibokapp.nibok.R
import io.realm.Realm
import org.jetbrains.anko.toast

/**
 * Base fragment implementing common features.
 *
 * It sets up the menu and creates stubs for handling menu actions.
 */
abstract class BaseFragment : Fragment() {

    companion object {
        val TAG: String = BaseFragment::class.java.simpleName
    }

    // Access to the realm DB, shared with subclasses
    protected var realm: Realm? = null

    override fun onStart() {
        super.onStart()
        // Get realm connection
        realm = Realm.getDefaultInstance()
        Log.d(TAG, "Started, got realm")
    }

    override fun onStop() {
        super.onStop()
        realm?.close()
        Log.d(TAG, "Stopping, closed realm")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Add options menu
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.toolbar_menu, menu)

        // Find the search view
        val searchAction = menu?.findItem(R.id.searchAction)
        val searchView = searchAction?.actionView as SearchView

        // Add search hint
        searchView.queryHint = getSearchHint()

        // Add listener to search view input
        searchView.setOnQueryTextListener(
                object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        Log.i(TAG, "Search submit: $query")
                        query?.let {
                            handleOnQueryTextSubmit(it)
                        }
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        Log.i(TAG, "Search change to: $newText")
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
        MenuItemCompat.setOnActionExpandListener(searchAction,
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
            R.id.settingsAction -> handleSettingsAction()
            else -> handleUnknownAction(item)
        }
        return true
    }

    /**
     * Handle the search action.
     */
    open fun handleSearchAction() {
        Log.i(TAG, "Searching")
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
     * Handle the settings action.
     */
    private fun handleSettingsAction() {
        context.toast("Settings")
    }

    /**
     * Handle every other action not directly handled.
     */
    private fun handleUnknownAction(item: MenuItem) {
        super.onOptionsItemSelected(item)
        context.toast("Unknown menu action")
    }

}


