package com.nibokapp.nibok.ui.fragment.common

import android.os.Bundle
import android.support.v4.app.Fragment
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

    // Lazy access to the realm DB, shared with subclasses
    protected val realm: Realm by lazy { Realm.getDefaultInstance() }

    override fun onStart() {
        super.onStart()
        // First access to get realm connection
        realm
    }

    override fun onStop() {
        super.onStop()
        realm.close()
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
        val searchView = menu?.findItem(R.id.searchAction)?.actionView as SearchView

        // Add search hint
        searchView.queryHint = getString(R.string.search_hint)

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


